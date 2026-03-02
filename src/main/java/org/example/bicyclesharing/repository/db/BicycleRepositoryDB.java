package org.example.bicyclesharing.repository.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.repository.BicycleRepository;

public class BicycleRepositoryDB extends BaseRepositoryDB<Bicycle, UUID> implements
    BicycleRepository {

  @Override
  public List<Bicycle> findByState(StateBicycle stateBicycle) {
    return List.of();
  }

  @Override
  public List<Bicycle> findByType(TypeBicycle type) {
    return List.of();
  }

  @Override
  protected String getTableName() {
    return "";
  }

  @Override
  protected UUID getId(Bicycle entity) {
    return null;
  }

  @Override
  protected Bicycle mapRow(ResultSet rs) throws SQLException {
    return null;
  }

  @Override
  protected Object[] getInsertValues(Bicycle entity) {
    return new Object[0];
  }

  @Override
  protected Object[] getUpdateValues(Bicycle entity) {
    return new Object[0];
  }

  @Override
  protected String getIdColumn() {
    return "";
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[0];
  }
}
