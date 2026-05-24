package org.example.bicyclesharing.repository.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.dto.LatestIssueInfo;
import org.example.bicyclesharing.repository.BikeIssueRepository;
import org.springframework.jdbc.core.RowMapper;

public class BikeIssueRepositoryDB extends BaseRepositoryDB<BikeIssue, UUID> implements BikeIssueRepository {

  public BikeIssueRepositoryDB() {
    super();
  }

  public BikeIssueRepositoryDB(javax.sql.DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected String getTableName() {
    return "bike_issues";
  }

  @Override
  protected String getCreateTableSQL() {
    return """
      CREATE TABLE IF NOT EXISTS bike_issues (
        id VARCHAR(36) PRIMARY KEY,
        rental_id VARCHAR(36) NOT NULL,
        bicycle_id VARCHAR(36) NOT NULL,
        problem_type VARCHAR(100) NOT NULL,
        comment VARCHAR(500),
        technical_problem BOOLEAN NOT NULL,
        created_at TIMESTAMP NOT NULL,
        status VARCHAR(50) NOT NULL
      )
    """;
  }

  @Override
  protected UUID getId(BikeIssue e) {
    return e.getId();
  }

  @Override
  protected RowMapper<BikeIssue> rowMapper() {
    return (rs, rowNum) -> BikeIssue.fromDatabase(
        UUID.fromString(rs.getString("id")),
        UUID.fromString(rs.getString("rental_id")),
        UUID.fromString(rs.getString("bicycle_id")),
        rs.getString("problem_type"),
        rs.getString("comment"),
        rs.getBoolean("technical_problem"),
        rs.getTimestamp("created_at").toLocalDateTime(),
        IssueStatus.valueOf(rs.getString("status"))
    );
  }

  @Override
  protected Object[] getInsertValues(BikeIssue e) {
    return new Object[]{
        e.getId().toString(),
        e.getRentalId().toString(),
        e.getBicycleId().toString(),
        e.getProblemType(),
        e.getComment(),
        e.isTechnicalProblem(),
        Timestamp.valueOf(e.getCreatedAt()),
        e.getStatus().name()
    };
  }

  @Override
  protected Object[] getUpdateValues(BikeIssue e) {
    return new Object[]{
        e.getRentalId().toString(),
        e.getBicycleId().toString(),
        e.getProblemType(),
        e.getComment(),
        e.isTechnicalProblem(),
        Timestamp.valueOf(e.getCreatedAt()),
        e.getStatus().name(),
        e.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[]{
        "rental_id",
        "bicycle_id",
        "problem_type",
        "comment",
        "technical_problem",
        "created_at",
        "status"
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  public LatestIssueInfo getLatestIssueInfo() {

    String sql = """
      SELECT
          b.model,
          i.problem_type,
          i.created_at
      FROM bike_issues i
      JOIN bicycles b ON b.id = i.bicycle_id
      ORDER BY i.created_at DESC
      LIMIT 1
    """;

    List<LatestIssueInfo> list = jdbcTemplate.query(sql, (rs, rowNum) ->
        new LatestIssueInfo(
            rs.getString("model"),
            rs.getString("problem_type"),
            rs.getTimestamp("created_at").toLocalDateTime()
        )
    );

    return list.isEmpty() ? null : list.getFirst();
  }

  @Override
  public long countTechnicalIssues() {
    String sql = """
    SELECT COUNT(*)
    FROM bike_issues
    WHERE technical_problem = TRUE
    """;

    Long res = jdbcTemplate.queryForObject(
        sql,
        Long.class
    );

    return res == null ? 0 : res;
  }

  @Override
  public List<BikeIssue> findByStatus(IssueStatus status) {
    return jdbcTemplate.query(
        "SELECT * FROM bike_issues WHERE status = ?",
        rowMapper(),
        status.name()
    );
  }

  @Override
  public List<BikeIssue> findByBicycleId(UUID bicycleId) {
    return jdbcTemplate.query(
        "SELECT * FROM bike_issues WHERE bicycle_id = ?",
        rowMapper(),
        bicycleId.toString()
    );
  }

  @Override
  public long countByIssueStatus(IssueStatus status) {
    Long res = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM bike_issues WHERE status = ?",
        Long.class,
        status.name()
    );
    return res == null ? 0 : res;
  }
}