package org.example.bicyclesharing.repository.db;

import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public final class TestDataSourceFactory {

  private TestDataSourceFactory() {
  }

  public static DataSource create() {
    String dbName = "test_db_" + UUID.randomUUID().toString().replace("-", "");

    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1;MODE=LEGACY");
    dataSource.setUsername("sa");
    dataSource.setPassword("");

    return dataSource;
  }
}
