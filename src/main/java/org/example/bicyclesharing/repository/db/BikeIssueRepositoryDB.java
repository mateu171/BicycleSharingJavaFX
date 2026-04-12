package org.example.bicyclesharing.repository.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.repository.BikeIssueRepository;
import org.springframework.jdbc.core.RowMapper;

public class BikeIssueRepositoryDB
    extends BaseRepositoryDB<BikeIssue, UUID>
    implements BikeIssueRepository {

  @Override
  public List<BikeIssue> findByStatus(IssueStatus status) {
    String sql = "SELECT * FROM BIKE_ISSUES WHERE status = ?";
    return jdbcTemplate.query(sql, rowMapper(), status.name());
  }

  @Override
  public List<BikeIssue> findByBicycleId(UUID bicycleId) {
    String sql = "SELECT * FROM BIKE_ISSUES WHERE bicycle_id = ?";
    return jdbcTemplate.query(sql, rowMapper(), bicycleId.toString());
  }

  @Override
  protected String getCreateTableSQL() {
    return "CREATE TABLE IF NOT EXISTS BIKE_ISSUES (" +
        "id VARCHAR(36) PRIMARY KEY," +
        "rental_id VARCHAR(36) NOT NULL," +
        "bicycle_id VARCHAR(36) NOT NULL," +
        "problem_type VARCHAR(100) NOT NULL," +
        "comment VARCHAR(500)," +
        "technical_problem BOOLEAN NOT NULL," +
        "created_at TIMESTAMP NOT NULL," +
        "status VARCHAR(50) NOT NULL" +
        ")";
  }

  @Override
  protected String getTableName() {
    return "BIKE_ISSUES";
  }

  @Override
  protected UUID getId(BikeIssue entity) {
    return entity.getId();
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
  protected Object[] getInsertValues(BikeIssue entity) {
    return new Object[] {
        entity.getId().toString(),
        entity.getRentalId().toString(),
        entity.getBicycleId(),toString(),
        entity.getProblemType(),
        entity.getComment(),
        entity.isTechnicalProblem(),
        Timestamp.valueOf(entity.getCreatedAt()),
        entity.getStatus().name()
    };
  }

  @Override
  protected Object[] getUpdateValues(BikeIssue entity) {
    return new Object[] {
        entity.getRentalId().toString(),
        entity.getBicycleId().toString(),
        entity.getProblemType(),
        entity.getComment(),
        entity.isTechnicalProblem(),
        Timestamp.valueOf(entity.getCreatedAt()),
        entity.getStatus().name(),
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[] {
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
}