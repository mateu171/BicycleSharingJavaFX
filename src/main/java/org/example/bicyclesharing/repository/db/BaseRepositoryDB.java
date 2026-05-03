package org.example.bicyclesharing.repository.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.util.Set;
import javax.sql.DataSource;
import org.example.bicyclesharing.exception.RepositoryException;
import org.example.bicyclesharing.repository.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public abstract class BaseRepositoryDB<T, ID> implements Repository<T, ID> {

  private static final Path DB_DIRECTORY = Path.of(System.getProperty("user.dir"), "db");
  private static final String DB_URL = "jdbc:h2:file:" + DB_DIRECTORY.resolve("bicyclesharing")
      .toAbsolutePath()
      .toString()
      .replace("\\", "/") +
      ";DB_CLOSE_DELAY=-1;" +
      "DB_CLOSE_ON_EXIT=FALSE";
  private static final Set<String> ALLOWED_COLUMNS = Set.of("model", "start_time", "login", "created_at");
  private static final Set<String> ALLOWED_DIRECTIONS = Set.of("ASC", "DESC");

  protected final JdbcTemplate jdbcTemplate;

  private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

  protected BaseRepositoryDB() {
    createDatabaseDirectory();
    this.jdbcTemplate = new JdbcTemplate(new DriverManagerDataSource(DB_URL, "sa", ""));
    initTable();
  }

  private void initTable() {
    String sql = getCreateTableSQL();
    jdbcTemplate.execute(sql);
  }

  private void createDatabaseDirectory() {
    try {
      Files.createDirectories(DB_DIRECTORY);
    } catch (IOException e) {
      throw new RepositoryException("Не вдалося створити папку для бази даних", e);
    }
  }

  public void executeInTransaction(Runnable action) {
    executeInTransaction(conn -> action.run());
  }

  public void executeInTransaction(TransactionCallback callback) {
    Connection connection = null;
    boolean previousAutoCommit = true;

    try {
      DataSource dataSource = jdbcTemplate.getDataSource();
      if (dataSource == null) {
        throw new RepositoryException("DataSource is null");
      }

      connection = dataSource.getConnection();
      previousAutoCommit = connection.getAutoCommit();
      connection.setAutoCommit(false);

      Connection existingConnection = currentConnection.get();
      currentConnection.set(connection);

      try {
        callback.execute(connection);
        connection.commit();
      } catch (Exception e) {
        connection.rollback();
        throw e;
      } finally {
        currentConnection.set(existingConnection);
      }

    } catch (Exception e) {
      throw new RepositoryException("Transaction failed", e);
    } finally {
      if (connection != null) {
        try {
          connection.setAutoCommit(previousAutoCommit);
          connection.close();
        } catch (SQLException e) {
        }
      }
    }
  }
  protected JdbcTemplate getCurrentJdbcTemplate() {
    Connection conn = currentConnection.get();
    if (conn != null) {
      return new JdbcTemplate(new org.springframework.jdbc.datasource.SingleConnectionDataSource(conn, false));
    }
    return jdbcTemplate;
  }

  @FunctionalInterface
  public interface TransactionCallback {
    void execute(Connection connection) throws SQLException;
  }


  @Override
  public T save(T entity) {
    String sql = getInsertSQL(entity);
    getCurrentJdbcTemplate().update(sql, getInsertValues(entity));
    return entity;
  }

  @Override
  public T update(T entity) {
    String[] columns = getUpdateColumns();
    StringBuilder setClause = new StringBuilder();

    for (int i = 0; i < columns.length; i++) {
      setClause.append(columns[i]).append("=?");
      if (i < columns.length - 1) {
        setClause.append(",");
      }
    }

    String sql = "UPDATE " + getTableName()
        + " SET " + setClause
        + " WHERE " + getIdColumn() + "=?";

    Object[] values = getUpdateValues(entity);
    getCurrentJdbcTemplate().update(sql, values);

    return entity;
  }

  @Override
  public List<T> findAll() {
    String sql = "SELECT * FROM " + getTableName();
    return getCurrentJdbcTemplate().query(sql, rowMapper());
  }

  @Override
  public Optional<T> findById(ID id) {
    System.out.println("[SQL] findById from " + getTableName());

    String sql = "SELECT * FROM " + getTableName()
        + " WHERE " + getIdColumn() + "=?";
    List<T> result = getCurrentJdbcTemplate().query(sql, rowMapper(), id);
    return result.stream().findFirst();
  }

  @Override
  public boolean deleteById(ID id) {
    String sql = "DELETE FROM " + getTableName()
        + " WHERE " + getIdColumn() + "=?";
    return getCurrentJdbcTemplate().update(sql, id) > 0;
  }

  @Override
  public boolean delete(T entity) {
    return deleteById(getId(entity));
  }

  @Override
  public boolean existsById(ID id) {
    String sql = "SELECT COUNT(*) FROM " + getTableName()
        + " WHERE " + getIdColumn() + "=?";
    Long count = getCurrentJdbcTemplate().queryForObject(sql, Long.class, id);
    return count != null && count > 0;
  }

  @Override
  public long count() {
    String sql = "SELECT COUNT(*) FROM " + getTableName();
    Long result = getCurrentJdbcTemplate().queryForObject(sql, Long.class);
    return result == null ? 0 : result;
  }


  protected static class QueryData {
    private final StringBuilder sql;
    private final List<Object> params = new ArrayList<>();

    public QueryData(String baseSql) {
      this.sql = new StringBuilder(baseSql);
    }

    public void addCondition(String condition, Object... values) {
      sql.append(" AND ").append(condition);
      params.addAll(Arrays.asList(values));
    }

    public void addLikeCondition(String column, String value) {
      if (value != null && !value.isBlank()) {
        sql.append(" AND LOWER(").append(column).append(") LIKE LOWER(?)");
        params.add("%" + value.trim() + "%");
      }
    }

    public void addEqualsCondition(String column, Object value) {
      if (value != null) {
        sql.append(" AND ").append(column).append(" = ?");
        params.add(value);
      }
    }

    public void addOrderBy(String column, String direction) {
      if (column != null && ALLOWED_COLUMNS.contains(column) &&
          direction != null && ALLOWED_DIRECTIONS.contains(direction.toUpperCase())) {
        sql.append(" ORDER BY ").append(column).append(" ").append(direction);
      }
    }

    public String getSql() {
      return sql.toString();
    }

    public Object[] getParams() {
      return params.toArray();
    }
  }


  protected abstract String getCreateTableSQL();
  protected abstract String getTableName();
  protected abstract ID getId(T entity);
  protected abstract RowMapper<T> rowMapper();
  protected abstract Object[] getInsertValues(T entity);
  protected abstract Object[] getUpdateValues(T entity);
  protected abstract String[] getUpdateColumns();
  protected abstract String getIdColumn();

  private String getInsertSQL(T entity) {
    int valuesCount = getInsertValues(entity).length;
    String placeholders = String.join(",", java.util.Collections.nCopies(valuesCount, "?"));
    return "INSERT INTO " + getTableName() + " VALUES(" + placeholders + ")";
  }
}