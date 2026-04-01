package org.example.bicyclesharing.repository.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
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
        "issue_id VARCHAR(36)," +
        "type VARCHAR(50) NOT NULL," +
        "description TEXT," +
        "result TEXT," +
        "returned_to_available BOOLEAN NOT NULL," +
        "written_off BOOLEAN NOT NULL," +
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
          rs.getString("issue_id") != null ? UUID.fromString(rs.getString("issue_id")) : null,
          MaintenanceType.valueOf(rs.getString("type")),
          rs.getString("description"),
          rs.getString("result"),
          rs.getBoolean("returned_to_available"),
          rs.getBoolean("written_off"),
          rs.getTimestamp("created_at").toLocalDateTime()
      );
  }

  @Override
  protected Object[] getInsertValues(MaintenanceRecord entity) {
    return new Object[] {
        entity.getId().toString(),
        entity.getBicycleId().toString(),
        entity.getMechanicId().toString(),
        entity.getIssueId() != null ? entity.getIssueId().toString() : null,
        entity.getType().name(),
        entity.getDescription(),
        entity.getResult(),
        entity.isReturnedToAvailable(),
        entity.isWrittenOff(),
        Timestamp.valueOf(entity.getCreatedAt())
    };
  }

  @Override
  protected Object[] getUpdateValues(MaintenanceRecord entity) {
    return new Object[] {
        entity.getBicycleId().toString(),
        entity.getMechanicId().toString(),
        entity.getIssueId() != null ? entity.getIssueId().toString() : null,
        entity.getType().name(),
        entity.getDescription(),
        entity.getResult(),
        entity.isReturnedToAvailable(),
        entity.isWrittenOff(),
        Timestamp.valueOf(entity.getCreatedAt()),
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[] {
        "bicycle_id",
        "mechanic_id",
        "issue_id",
        "type",
        "description",
        "result",
        "returned_to_available",
        "written_off",
        "created_at"
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }
}