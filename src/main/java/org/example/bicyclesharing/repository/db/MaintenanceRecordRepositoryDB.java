package org.example.bicyclesharing.repository.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.domain.enums.MaintenanceAction;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.example.bicyclesharing.repository.MaintenanceRecordRepository;
import org.springframework.jdbc.core.RowMapper;

public class MaintenanceRecordRepositoryDB
    extends BaseRepositoryDB<MaintenanceRecord, UUID>
    implements MaintenanceRecordRepository {

  @Override
  public List<MaintenanceRecord> findByBicycleId(UUID bicycleId) {
    String sql = "SELECT * FROM MAINTENANCE_RECORDS WHERE bicycle_id = ?";
    return jdbcTemplate.query(sql, rowMapper(), bicycleId.toString());
  }

  @Override
  protected String getCreateTableSQL() {
    return "CREATE TABLE IF NOT EXISTS MAINTENANCE_RECORDS (" +
        "id VARCHAR(36) PRIMARY KEY," +
        "bicycle_id VARCHAR(36) NOT NULL," +
        "mechanic_id VARCHAR(36) NOT NULL," +
        "type VARCHAR(50) NOT NULL," +
        "description TEXT," +
        "result TEXT," +
        "action VARCHAR(50) NOT NULL," +
        "created_at TIMESTAMP NOT NULL" +
        ")";
  }

  @Override
  protected String getTableName() {
    return "MAINTENANCE_RECORDS";
  }

  @Override
  protected UUID getId(MaintenanceRecord entity) {
    return entity.getId();
  }

  @Override
  protected RowMapper<MaintenanceRecord> rowMapper() {
    return (rs, rowNum) ->
      MaintenanceRecord.fromDatabase(
          UUID.fromString(rs.getString("id")),
          UUID.fromString(rs.getString("bicycle_id")),
          UUID.fromString(rs.getString("mechanic_id")),
          MaintenanceType.valueOf(rs.getString("type")),
          rs.getString("description"),
          rs.getString("result"),
          MaintenanceAction.valueOf(rs.getString("action")),
          rs.getTimestamp("created_at").toLocalDateTime()
      );
  }

  @Override
  protected Object[] getInsertValues(MaintenanceRecord entity) {
    return new Object[] {
        entity.getId().toString(),
        entity.getBicycleId().toString(),
        entity.getMechanicId().toString(),
        entity.getType().name(),
        entity.getDescription(),
        entity.getResult(),
        entity.getAction().name(),
        Timestamp.valueOf(entity.getCreatedAt())
    };
  }

  @Override
  protected Object[] getUpdateValues(MaintenanceRecord entity) {
    return new Object[] {
        entity.getBicycleId().toString(),
        entity.getMechanicId().toString(),
        entity.getType().name(),
        entity.getDescription(),
        entity.getResult(),
        entity.getAction().name(),
        Timestamp.valueOf(entity.getCreatedAt()),
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[] {
        "bicycle_id",
        "mechanic_id",
        "type",
        "description",
        "result",
        "action",
        "created_at"
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }
}