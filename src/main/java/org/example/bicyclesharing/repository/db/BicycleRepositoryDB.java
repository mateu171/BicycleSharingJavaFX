package org.example.bicyclesharing.repository.db;

import java.util.List;
import java.util.UUID;

import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.repository.BicycleRepository;
import org.springframework.jdbc.core.RowMapper;

public class BicycleRepositoryDB
    extends BaseRepositoryDB<Bicycle, UUID>
    implements BicycleRepository {

  @Override
  public List<Bicycle> findByState(StateBicycle stateBicycle) {

    String sql = "SELECT * FROM BICYCLES WHERE state=?";

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

      Bicycle bicycle = new Bicycle(
          rs.getString("model"),
          String.valueOf(rs.getDouble("price_per_hour")),
          rs.getDouble("latitude"),
          rs.getDouble("longitude")
      );

      bicycle.setId(UUID.fromString(rs.getString("id")));

      bicycle.setState(
          StateBicycle.valueOf(rs.getString("state"))
      );

      return bicycle;
    };
  }

  @Override
  protected Object[] getInsertValues(Bicycle entity) {

    if (entity == null) return new Object[8];

    return new Object[]{
        entity.getId(),
        entity.getModel(),
        entity.getTypeBicycle().name(),
        entity.getState().name(),
        entity.getPricePerHour(),
        entity.getRentalId(),
        entity.getLatitude(),
        entity.getLongitude()
    };
  }

  @Override
  protected Object[] getUpdateValues(Bicycle entity) {

    return new Object[]{
        entity.getModel(),
        entity.getTypeBicycle().name(),
        entity.getState().name(),
        entity.getPricePerHour(),
        entity.getRentalId(),
        entity.getLatitude(),
        entity.getLongitude(),
        entity.getId()
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  protected String[] getUpdateColumns() {

    return new String[]{
        "model",
        "type_bicycle",
        "state",
        "price_per_hour",
        "rental_id",
        "latitude",
        "longitude"
    };
  }
}