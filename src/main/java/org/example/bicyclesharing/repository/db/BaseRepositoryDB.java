package org.example.bicyclesharing.repository.db;

import org.example.bicyclesharing.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepositoryDB<T, ID> implements Repository<T, ID> {

  private final String URL = "jdbc:h2:~/test";
  private final String username = "sa";
  private final String password = "";

  protected Connection getConnect() throws SQLException {
    return DriverManager.getConnection(URL, username, password);
  }

  protected abstract String getTableName();

  protected abstract ID getId(T entity);

  protected abstract T mapRow(ResultSet rs) throws SQLException;

  protected abstract Object[] getInsertValues(T entity);

  protected abstract Object[] getUpdateValues(T entity);

  protected abstract String getIdColumn();

  @Override
  public T save(T entity) {
    String sql = getInsertSQL();
    try (Connection conn = getConnect();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      Object[] values = getInsertValues(entity);
      for (int i = 0; i < values.length; i++) {
        stmt.setObject(i + 1, values[i]);
      }
      stmt.executeUpdate();
      return entity;

    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  private String getInsertSQL() {
    Object[] values = getInsertValues(null); // беремо кількість полів
    String placeholders = String.join(",", java.util.Collections.nCopies(values.length, "?"));
    return "INSERT INTO " + getTableName() + " VALUES(" + placeholders + ")";
  }

  @Override
  public T update(T entity) {
    Object[] values = getUpdateValues(entity);
    String[] columns = getUpdateColumns();
    StringBuilder setClause = new StringBuilder();
    for (int i = 0; i < columns.length; i++) {
      setClause.append(columns[i]).append("=?");
      if (i < columns.length - 1) setClause.append(",");
    }
    String sql = "UPDATE " + getTableName() + " SET " + setClause + " WHERE " + getIdColumn() + "=?";
    try (Connection conn = getConnect();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      for (int i = 0; i < values.length; i++) {
        stmt.setObject(i + 1, values[i]);
      }
      stmt.executeUpdate();
      return entity;

    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  protected abstract String[] getUpdateColumns();

  @Override
  public List<T> findAll() {
    List<T> list = new ArrayList<>();
    String sql = "SELECT * FROM " + getTableName();
    try (Connection conn = getConnect();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        list.add(mapRow(rs));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return list;
  }

  @Override
  public Optional<T> findById(ID id) {
    String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumn() + "=?";
    try (Connection conn = getConnect();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setObject(1, id);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) return Optional.of(mapRow(rs));

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public boolean deleteById(ID id) {
    String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumn() + "=?";
    try (Connection conn = getConnect();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setObject(1, id);
      return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean delete(T entity) {
    return deleteById(getId(entity));
  }

  @Override
  public boolean existsById(ID id) {
    return findById(id).isPresent();
  }

  @Override
  public long count() {
    String sql = "SELECT COUNT(*) FROM " + getTableName();
    try (Connection conn = getConnect();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      if (rs.next()) return rs.getLong(1);

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return 0;
  }
}