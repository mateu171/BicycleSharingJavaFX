package org.example.bicyclesharing.repository.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.repository.Repository;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public abstract class BaseRepositoryDB<T, ID> implements Repository<T, ID> {

  protected final JdbcTemplate jdbcTemplate;

  public BaseRepositoryDB() {
    this.jdbcTemplate = new JdbcTemplate(new DriverManagerDataSource(
        "jdbc:h2:file:C:/Users/Asus/bicyclesharing",
        "sa",
        ""
    ));
    initTable();
  }

  private void initTable() {
    String sql = getCreateTableSQL();
    jdbcTemplate.execute(sql);
  }

  protected abstract String getCreateTableSQL();
  protected abstract String getTableName();
  protected abstract ID getId(T entity);
  protected abstract RowMapper<T> rowMapper();
  protected abstract Object[] getInsertValues(T entity);
  protected abstract Object[] getUpdateValues(T entity);
  protected abstract String[] getUpdateColumns();
  protected abstract String getIdColumn();

  @Override
  public T save(T entity) {
    String sql = getInsertSQL(entity);
    jdbcTemplate.update(sql, getInsertValues(entity));
    return entity;
  }

  private String getInsertSQL(T entity) {
    int valuesCount = getInsertValues(entity).length;

    String placeholders = String.join(
        ",",
        java.util.Collections.nCopies(valuesCount, "?")
    );

    return "INSERT INTO " + getTableName() + " VALUES(" + placeholders + ")";
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
    jdbcTemplate.update(sql, values);

    return entity;
  }

  @Override
  public List<T> findAll() {
    String sql = "SELECT * FROM " + getTableName();
    return jdbcTemplate.query(sql, rowMapper());
  }

  @Override
  public Optional<T> findById(ID id) {
    String sql = "SELECT * FROM " + getTableName()
        + " WHERE " + getIdColumn() + "=?";

    List<T> result = jdbcTemplate.query(sql, rowMapper(), id);
    return result.stream().findFirst();
  }

  @Override
  public boolean deleteById(ID id) {
    String sql = "DELETE FROM " + getTableName()
        + " WHERE " + getIdColumn() + "=?";
    return jdbcTemplate.update(sql, id) > 0;
  }

  @Override
  public boolean delete(T entity) {
    return deleteById(getId(entity));
  }

  @Override
  public boolean existsById(ID id) {
    String sql = "SELECT COUNT(*) FROM " + getTableName()
        + " WHERE " + getIdColumn() + "=?";

    Long count = jdbcTemplate.queryForObject(sql, Long.class, id);
    return count != null && count > 0;
  }

  @Override
  public long count() {
    String sql = "SELECT COUNT(*) FROM " + getTableName();
    Long result = jdbcTemplate.queryForObject(sql, Long.class);
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

    public void addOrderBy(String orderBy) {
      if (orderBy != null && !orderBy.isBlank()) {
        sql.append(" ORDER BY ").append(orderBy);
      }
    }

    public String getSql() {
      return sql.toString();
    }

    public Object[] getParams() {
      return params.toArray();
    }
  }
}
