package org.example.bicyclesharing.repository.db;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.repository.BicycleRepository;
import org.springframework.jdbc.core.RowMapper;

public class BicycleRepositoryDB
    extends BaseRepositoryDB<Bicycle, UUID>
    implements BicycleRepository {

  @Override
  public List<Bicycle> findByState(StateBicycle stateBicycle) {
    String sql = "SELECT * FROM BICYCLES WHERE state = ?";
    return jdbcTemplate.query(sql, rowMapper(), stateBicycle.name());
  }

  @Override
  protected String getTableName() {
    return "BICYCLES";
  }

  @Override
  protected UUID getId(Bicycle entity) {
    return entity.getId();
  }

  @Override
  protected RowMapper<Bicycle> rowMapper() {
    return (rs, rowNum) -> {
      String rentalIdRaw = rs.getString("rental_id");
      UUID rentalId = rentalIdRaw != null && !rentalIdRaw.isBlank()
          ? UUID.fromString(rentalIdRaw)
          : null;

      String stationIdRaw = rs.getString("station_id");
      UUID stationId = stationIdRaw != null && !stationIdRaw.isBlank()
          ? UUID.fromString(stationIdRaw)
          : null;

      return Bicycle.fromDatabase(
          UUID.fromString(rs.getString("id")),
          rs.getString("model"),
          TypeBicycle.valueOf(rs.getString("type_bicycle")),
          StateBicycle.valueOf(rs.getString("state")),
          rs.getDouble("price_per_minute"),
          rentalId,
          stationId
      );
    };
  }

  @Override
  protected Object[] getInsertValues(Bicycle entity) {
    if (entity == null) return new Object[7];

    return new Object[] {
        entity.getId().toString(),
        entity.getModel(),
        entity.getTypeBicycle().name(),
        entity.getState().name(),
        entity.getPricePerMinute(),
        entity.getRentalId() != null ? entity.getRentalId().toString() : null,
        entity.getStationId() != null ? entity.getStationId().toString() : null
    };
  }

  @Override
  protected Object[] getUpdateValues(Bicycle entity) {
    return new Object[] {
        entity.getModel(),
        entity.getTypeBicycle().name(),
        entity.getState().name(),
        entity.getPricePerMinute(),
        entity.getRentalId() != null ? entity.getRentalId().toString() : null,
        entity.getStationId() != null ? entity.getStationId().toString() : null,
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
        "model",
        "type_bicycle",
        "state",
        "price_per_minute",
        "rental_id",
        "station_id"
    };
  }

  @Override
  protected String getCreateTableSQL() {
    return "CREATE TABLE IF NOT EXISTS BICYCLES (" +
        "id VARCHAR(36) PRIMARY KEY, " +
        "model VARCHAR(255) NOT NULL, " +
        "type_bicycle VARCHAR(50) NOT NULL, " +
        "state VARCHAR(50) NOT NULL, " +
        "price_per_minute DOUBLE NOT NULL, " +
        "rental_id VARCHAR(36), " +
        "station_id VARCHAR(36) NOT NULL" +
        ")";
  }
}