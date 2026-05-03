package org.example.bicyclesharing.repository;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransactionCallback {
  void execute(Connection connection) throws SQLException;
}
