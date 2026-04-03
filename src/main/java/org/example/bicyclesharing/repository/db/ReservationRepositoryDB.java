package org.example.bicyclesharing.repository.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.enums.DocumentType;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.repository.ReservationRepository;
import org.springframework.jdbc.core.RowMapper;

public class ReservationRepositoryDB extends BaseRepositoryDB<Reservation, UUID> implements
    ReservationRepository {

  @Override
  protected String getCreateTableSQL() {
    return "CREATE TABLE IF NOT EXISTS RESERVATIONS (" +
        "id VARCHAR(36) PRIMARY KEY," +
        "customer_id VARCHAR(36) NOT NULL," +
        "bicycle_id VARCHAR(36) NOT NULL," +
        "manager_id VARCHAR(36) NOT NULL," +
        "start_time TIMESTAMP NOT NULL," +
        "end_time TIMESTAMP NOT NULL," +
        "document_type VARCHAR(50) NOT NULL," +
        "document_number VARCHAR(50) NOT NULL," +
        "deposit_amount DOUBLE NOT NULL," +
        "deposit_paid BOOLEAN NOT NULL," +
        "inventory_issued BOOLEAN NOT NULL," +
        "status VARCHAR(50) NOT NULL" +
        ")";
  }

  @Override
  protected String getTableName() {
    return "RESERVATIONS";
  }

  @Override
  protected UUID getId(Reservation entity) {
    return entity.getId();
  }

  @Override
  protected RowMapper<Reservation> rowMapper() {
    return (rs, rowNum) -> Reservation.fromDatabase(
        UUID.fromString(rs.getString("id")),
        UUID.fromString(rs.getString("customer_id")),
        UUID.fromString(rs.getString("bicycle_id")),
        UUID.fromString(rs.getString("manager_id")),
        rs.getTimestamp("start_time").toLocalDateTime(),
        rs.getTimestamp("end_time").toLocalDateTime(),
        DocumentType.valueOf(rs.getString("document_type")),
        rs.getString("document_number"),
        rs.getDouble("deposit_amount"),
        rs.getBoolean("deposit_paid"),
        rs.getBoolean("inventory_issued"),
        ReservationStatus.valueOf(rs.getString("status"))
    );
  }

  @Override
  protected Object[] getInsertValues(Reservation entity) {
    return new Object[] {
        entity.getId().toString(),
        entity.getCustomerId().toString(),
        entity.getBicycleId().toString(),
        entity.getManagerId().toString(),
        Timestamp.valueOf(entity.getStartTime()),
        Timestamp.valueOf(entity.getEndTime()),
        entity.getDocumentType().name(),
        entity.getDocumentNumber(),
        entity.getDepositAmount(),
        entity.isDepositPaid(),
        entity.isInventoryIssued(),
        entity.getStatus().name()
    };
  }

  @Override
  protected Object[] getUpdateValues(Reservation entity) {
    return new Object[] {
        entity.getCustomerId().toString(),
        entity.getBicycleId().toString(),
        entity.getManagerId().toString(),
        Timestamp.valueOf(entity.getStartTime()),
        Timestamp.valueOf(entity.getEndTime()),
        entity.getDocumentType().name(),
        entity.getDocumentNumber(),
        entity.getDepositAmount(),
        entity.isDepositPaid(),
        entity.isInventoryIssued(),
        entity.getStatus().name(),
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[] {
        "customer_id",
        "bicycle_id",
        "manager_id",
        "start_time",
        "end_time",
        "document_type",
        "document_number",
        "deposit_amount",
        "deposit_paid",
        "inventory_issued",
        "status"
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  public List<Reservation> findByReservationStatus(ReservationStatus status) {
    String sql = "SELECT * FROM RESERVATIONS WHERE status = ?";
    return jdbcTemplate.query(sql, rowMapper(), status.name());
  }
}