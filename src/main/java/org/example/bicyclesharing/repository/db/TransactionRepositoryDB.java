package org.example.bicyclesharing.repository.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.example.bicyclesharing.domain.Impl.Transaction;
import org.example.bicyclesharing.domain.enums.TransactionType;
import org.example.bicyclesharing.repository.TransactionRepository;

public class TransactionRepositoryDB
    extends BaseRepositoryDB<Transaction, UUID>
    implements TransactionRepository {

  @Override
  public List<Transaction> findByUserId(UUID userId) {

    List<Transaction> transactions = new ArrayList<>();

    String sql = "SELECT * FROM TRANSACTIONS WHERE USER_ID = ? ORDER BY TIMESTAMP DESC";

    try (PreparedStatement stmt = getConnect().prepareStatement(sql)) {

      stmt.setObject(1, userId);

      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        transactions.add(mapRow(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return transactions;
  }

  @Override
  protected String getTableName() {
    return "TRANSACTIONS";
  }

  @Override
  protected UUID getId(Transaction entity) {
    return entity.getId();
  }

  @Override
  protected String getIdColumn() {
    return "ID";
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[]{
        "USER_ID",
        "AMOUNT",
        "TYPE",
        "TIMESTAMP",
        "DESCRIPTION"
    };
  }

  @Override
  protected Transaction mapRow(ResultSet rs) throws SQLException {

    Transaction transaction = new Transaction(
        rs.getObject("USER_ID", UUID.class),
        rs.getDouble("AMOUNT"),
        TransactionType.valueOf(rs.getString("TYPE")),
        rs.getTimestamp("TIMESTAMP").toLocalDateTime(),
        rs.getString("DESCRIPTION")
    );

    transaction.setId(rs.getObject("ID", UUID.class));

    return transaction;
  }

  @Override
  protected Object[] getInsertValues(Transaction entity) {
    return new Object[]{
        entity.getId(),
        entity.getUserId(),
        entity.getAmount(),
        entity.getType().name(),
        Timestamp.valueOf(entity.getTimestamp()),
        entity.getDescription()
    };
  }

  @Override
  protected Object[] getUpdateValues(Transaction entity) {
    return new Object[]{
        entity.getUserId(),
        entity.getAmount(),
        entity.getType().name(),
        Timestamp.valueOf(entity.getTimestamp()),
        entity.getDescription(),
        entity.getId()
    };
  }
}