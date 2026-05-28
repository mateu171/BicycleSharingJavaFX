package org.example.bicyclesharing.repository.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.dto.LatestRentalInfo;
import org.example.bicyclesharing.repository.RentalRepository;
import org.springframework.jdbc.core.RowMapper;

public class RentalRepositoryDB extends BaseRepositoryDB<Rental, UUID> implements RentalRepository {

  public RentalRepositoryDB() {
    super();
  }

  public RentalRepositoryDB(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected String getTableName() {
    return "RENTALS";
  }

  @Override
  protected String getCreateTableSQL() {
    return """
            CREATE TABLE IF NOT EXISTS rentals (
                id VARCHAR(36) PRIMARY KEY,
                customer_id VARCHAR(36) NOT NULL,
                bicycle_id VARCHAR(36) NOT NULL,
                start_time TIMESTAMP NOT NULL,
                end_time TIMESTAMP,
                total_cost DOUBLE,
                is_deleted BOOLEAN DEFAULT FALSE NOT NULL
            )
            """;
  }

  @Override
  protected UUID getId(Rental entity) {
    return entity.getId();
  }

  @Override
  protected RowMapper<Rental> rowMapper() {
    return (rs, rowNum) -> {
      Rental rental = new Rental();

      rental.setId(UUID.fromString(rs.getString("id")));
      rental.setCustomerId(UUID.fromString(rs.getString("customer_id")));
      rental.setBicycleId(UUID.fromString(rs.getString("bicycle_id")));
      rental.setStart(rs.getTimestamp("start_time").toLocalDateTime());

      Timestamp endTs = rs.getTimestamp("end_time");
      if (endTs != null) {
        rental.setEnd(endTs.toLocalDateTime());
      }

      rental.setTotalCost(rs.getDouble("total_cost"));
      return rental;
    };
  }

  @Override
  protected Object[] getInsertValues(Rental entity) {
    return new Object[]{
        entity.getId().toString(),
        entity.getCustomerId().toString(),
        entity.getBicycleId().toString(),
        Timestamp.valueOf(entity.getStart()),
        entity.getEnd() != null ? Timestamp.valueOf(entity.getEnd()) : null,
        entity.getTotalCost(),
        false
    };
  }

  @Override
  protected Object[] getUpdateValues(Rental entity) {
    return new Object[]{
        entity.getCustomerId().toString(),
        entity.getBicycleId().toString(),
        Timestamp.valueOf(entity.getStart()),
        entity.getEnd() != null ? Timestamp.valueOf(entity.getEnd()) : null,
        entity.getTotalCost(),
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[]{"customer_id", "bicycle_id", "start_time", "end_time", "total_cost"};
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  public List<Rental> findByCustomerId(UUID id) {
    String sql = "SELECT * FROM RENTALS WHERE is_deleted = FALSE AND customer_id = ?";
    return jdbcTemplate.query(sql, rowMapper(), id.toString());
  }

  @Override
  public long countActiveRentals() {
    String sql = """
            SELECT COUNT(*)
            FROM RENTALS
            WHERE is_deleted = FALSE AND end_time IS NULL
            """;

    Long count = jdbcTemplate.queryForObject(sql, Long.class);
    return count != null ? count : 0;
  }

  @Override
  public LatestRentalInfo getLatestRentalInfo() {
    String sql = """
            SELECT
                c.full_name,
                b.model,
                r.start_time as start
            FROM RENTALS r
            JOIN CUSTOMERS c ON c.id = r.customer_id
            JOIN BICYCLES b ON b.id = r.bicycle_id
            WHERE r.is_deleted = FALSE
            ORDER BY r.start_time DESC
            LIMIT 1
            """;

    List<LatestRentalInfo> result = jdbcTemplate.query(
        sql,
        (rs, rowNum) -> new LatestRentalInfo(
            rs.getString("full_name"),
            rs.getString("model"),
            rs.getTimestamp("start").toLocalDateTime()
        )
    );

    return result.isEmpty() ? null : result.getFirst();
  }

  @Override
  public List<Rental> findByFilters(String search) {
    QueryData query = new QueryData("""
            SELECT r.*
            FROM RENTALS r
            JOIN CUSTOMERS c ON c.id = r.customer_id
            JOIN BICYCLES b ON b.id = r.bicycle_id
            WHERE r.is_deleted = FALSE
            """);

    if (search != null && !search.isBlank()) {
      String pattern = "%" + search.trim() + "%";
      query.addCondition(
          "(LOWER(c.full_name) LIKE LOWER(?) OR LOWER(b.model) LIKE LOWER(?))",
          pattern, pattern
      );
    }

    query.addOrderBy("r.start_time", "DESC");

    return jdbcTemplate.query(query.getSql(), rowMapper(), query.getParams());
  }
}