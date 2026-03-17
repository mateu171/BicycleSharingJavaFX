package org.example.bicyclesharing.repository.db;

import org.example.bicyclesharing.repository.Repository;

import java.util.List;
import java.util.Optional;
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

  // Метод для створення таблиці, якщо її немає
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

    String sql = getInsertSQL();

    jdbcTemplate.update(sql, getInsertValues(entity));

    return entity;
  }

  private String getInsertSQL() {

    int valuesCount = getInsertValues(null).length;

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
      if (i < columns.length - 1) setClause.append(",");
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

    String sql = "SELECT * FROM "
        + getTableName()
        + " WHERE "
        + getIdColumn()
        + "=?";

    List<T> result = jdbcTemplate.query(sql, rowMapper(), id);

    return result.stream().findFirst();
  }

  @Override
  public boolean deleteById(ID id) {

    String sql = "DELETE FROM "
        + getTableName()
        + " WHERE "
        + getIdColumn()
        + "=?";

    return jdbcTemplate.update(sql, id) > 0;
  }

  @Override
  public boolean delete(T entity) {

    return deleteById(getId(entity));
  }

  @Override
  public boolean existsById(ID id) {

    String sql = "SELECT COUNT(*) FROM "
        + getTableName()
        + " WHERE "
        + getIdColumn()
        + "=?";

    Long count = jdbcTemplate.queryForObject(sql, Long.class, id);

    return count != null && count > 0;
  }

  @Override
  public long count() {

    String sql = "SELECT COUNT(*) FROM " + getTableName();

    Long result = jdbcTemplate.queryForObject(sql, Long.class);

    return result == null ? 0 : result;
  }
}