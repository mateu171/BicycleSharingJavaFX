package org.example.bicyclesharing.repository.db;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractRepositoryTest {

  protected DataSource dataSource;
  protected JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUpDatabase()
  {
    dataSource = TestDataSourceFactory.create();
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  protected long countRowsInTable(String tableName)
  {
    Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName,Long.class);

    return count == null ? 0 : count;
  }
}
