package org.example.bicyclesharing.repository.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;

import org.example.bicyclesharing.exception.RepositoryException;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.TransactionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public abstract class BaseRepositoryDB<T, ID> implements Repository<T, ID> {

  private static final Path DB_DIRECTORY = Path.of(System.getProperty("user.dir"), "db");

  private static final String DB_URL =
      "jdbc:h2:file:" +
          DB_DIRECTORY.resolve("bicyclesharing")
              .toAbsolutePath()
              .toString()
              .replace("\\", "/") +
          ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

  private static final Set<String> ALLOWED_COLUMNS = Set.of(
      "model", "start_time", "login", "created_at", "name"
  );

  private static final Set<String> ALLOWED_DIRECTIONS = Set.of("ASC", "DESC");

  protected final JdbcTemplate jdbcTemplate;
  private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

  protected BaseRepositoryDB() {
    createDatabaseDirectory();
    this.jdbcTemplate = new JdbcTemplate(new DriverManagerDataSource(DB_URL, "sa", ""));
    initTable();
  }

  protected BaseRepositoryDB(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    initTable();
  }

  private void initTable() {
    jdbcTemplate.execute(getCreateTableSQL());
  }

  private void createDatabaseDirectory() {
    try {
      Files.createDirectories(DB_DIRECTORY);
    } catch (IOException e) {
      throw new RepositoryException("Cannot create DB folder", e);
    }
  }

  protected JdbcTemplate getCurrentJdbcTemplate() {
    Connection conn = currentConnection.get();
    if (conn != null) {
      return new JdbcTemplate(new org.springframework.jdbc.datasource.SingleConnectionDataSource(conn, false));
    }
    return jdbcTemplate;
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

  @Override
  public T save(T entity) {
    String sql = getInsertSQL(entity);
    getCurrentJdbcTemplate().update(sql, getInsertValues(entity));
    return entity;
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
  public T update(T entity) {
    String[] columns = getUpdateColumns();
    StringBuilder set = new StringBuilder();

    for (int i = 0; i < columns.length; i++) {
      set.append(columns[i]).append("=?");
      if (i < columns.length - 1) set.append(",");
    }

    String sql = "UPDATE " + getTableName() +
        " SET " + set +
        " WHERE " + getIdColumn() + "=?";

    getCurrentJdbcTemplate().update(sql, getUpdateValues(entity));
    return entity;
  }

  @Override
  public List<T> findAll() {
    return jdbcTemplate.query("SELECT * FROM " + getTableName(), rowMapper());
  }

  @Override
  public Optional<T> findById(ID id) {
    String sql = "SELECT * FROM " + getTableName() +
        " WHERE " + getIdColumn() + "=?";
    return jdbcTemplate.query(sql, rowMapper(), id).stream().findFirst();
  }

  @Override
  public boolean deleteById(ID id) {
    return jdbcTemplate.update(
        "DELETE FROM " + getTableName() + " WHERE " + getIdColumn() + "=?",
        id
    ) > 0;
  }

  @Override
  public long count() {
    Long res = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM " + getTableName(),
        Long.class
    );
    return res == null ? 0 : res;
  }

  protected String getInsertSQL(T entity) {
    int n = getInsertValues(entity).length;
    return "INSERT INTO " + getTableName() +
        " VALUES(" + String.join(",", Collections.nCopies(n, "?")) + ")";
  }

  protected static class QueryData {
    private final StringBuilder sql;
    private final List<Object> params = new ArrayList<>();

    public QueryData(String base) {
      this.sql = new StringBuilder(base);
    }

    public void addCondition(String c, Object... v) {
      sql.append(" AND ").append(c);
      params.addAll(Arrays.asList(v));
    }

    public void addLikeCondition(String col, String val) {
      if (val != null && !val.isBlank()) {
        sql.append(" AND LOWER(").append(col).append(") LIKE LOWER(?)");
        params.add("%" + val.trim() + "%");
      }
    }

    public void addEqualsCondition(String col, Object val) {
      if (val != null) {
        sql.append(" AND ").append(col).append("=?");
        params.add(val);
      }
    }

    public void addOrderBy(String col, String dir) {
      if (ALLOWED_COLUMNS.contains(col) && ALLOWED_DIRECTIONS.contains(dir.toUpperCase())) {
        sql.append(" ORDER BY ").append(col).append(" ").append(dir);
      }
    }

    public String getSql() { return sql.toString(); }
    public Object[] getParams() { return params.toArray(); }
  }

  protected abstract String getCreateTableSQL();
  protected abstract String getTableName();
  protected abstract ID getId(T entity);
  protected abstract RowMapper<T> rowMapper();
  protected abstract Object[] getInsertValues(T entity);
  protected abstract Object[] getUpdateValues(T entity);
  protected abstract String[] getUpdateColumns();
  protected abstract String getIdColumn();
}