package org.example.bicyclesharing.repository.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.repository.RentalRepository;
import org.springframework.jdbc.core.RowMapper;

public class RentalRepositoryDB
    extends BaseRepositoryDB<Rental, UUID>
    implements RentalRepository {

  @Override
  public List<Rental> findByUserId(UUID id) {
    String sql = "SELECT * FROM RENTALS WHERE userId = ?";
    return jdbcTemplate.query(sql, rowMapper(), id.toString());
  }

  @Override
  protected String getTableName() {
    return "RENTALS";
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
      rental.setUserId(UUID.fromString(rs.getString("userId")));
      rental.setBicycleId(UUID.fromString(rs.getString("bicycleId")));
      rental.setStart(rs.getTimestamp("start").toLocalDateTime());

      Timestamp endTs = rs.getTimestamp("endTime");
      if (endTs != null) {
        rental.setEnd(endTs.toLocalDateTime());
      }

      rental.setTotalCost(rs.getDouble("totalCost"));
      return rental;
    };
  }

  @Override
  protected Object[] getInsertValues(Rental entity) {
    return new Object[] {
        entity.getId().toString(),
        entity.getUserId().toString(),
        entity.getBicycleId().toString(),
        Timestamp.valueOf(entity.getStart()),
        entity.getEnd() != null ? Timestamp.valueOf(entity.getEnd()) : null,
        entity.getTotalCost()
    };
  }

  @Override
  protected Object[] getUpdateValues(Rental entity) {
    return new Object[] {
        entity.getUserId().toString(),
        entity.getBicycleId().toString(),
        Timestamp.valueOf(entity.getStart()),
        entity.getEnd() != null ? Timestamp.valueOf(entity.getEnd()) : null,
        entity.getTotalCost(),
        entity.getId().toString()
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[] {
        "userId",
        "bicycleId",
        "start",
        "endTime",
        "totalCost"
    };
  }

  @Override
  protected String getCreateTableSQL() {
    return "CREATE TABLE IF NOT EXISTS RENTALS (" +
        "id VARCHAR(36) PRIMARY KEY," +
        "userId VARCHAR(36) NOT NULL," +
        "bicycleId VARCHAR(36) NOT NULL," +
        "start TIMESTAMP NOT NULL," +
        "endTime TIMESTAMP," +
        "totalCost DOUBLE" +
        ")";
  }
}