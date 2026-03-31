package org.example.bicyclesharing.repository.db;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Employee;
import org.example.bicyclesharing.repository.EmployeeRepository;
import org.springframework.jdbc.core.RowMapper;

public class EmployeeRepositoryDB extends BaseRepositoryDB<Employee, UUID> implements EmployeeRepository {

  @Override
  public List<Employee> findByStationId(UUID id) {
    String sql = "SELECT * FROM EMPLOYEES WHERE station_id = ?";
    return jdbcTemplate.query(sql, rowMapper(), id.toString());
  }

  @Override
  public List<Employee> findByName(String name) {
    String sql = "SELECT * FROM EMPLOYEES WHERE LOWER(name) LIKE LOWER(?)";
    return jdbcTemplate.query(sql, rowMapper(), "%" + name + "%");
  }

  @Override
  protected String getCreateTableSQL() {
    return "CREATE TABLE IF NOT EXISTS EMPLOYEES (" +
        "id VARCHAR(36) PRIMARY KEY," +
        "name VARCHAR(255) NOT NULL," +
        "phone_number VARCHAR(50) NOT NULL," +
        "station_id VARCHAR(36)," +
        "salary DOUBLE NOT NULL" +
        ")";
  }

  @Override
  protected String getTableName() {
    return "EMPLOYEES";
  }

  @Override
  protected UUID getId(Employee entity) {
    return entity.getId();
  }

  @Override
  protected RowMapper<Employee> rowMapper() {
    return (rs, rowNum) -> {
      Employee employee = new Employee(
          rs.getString("name"),
          rs.getString("phone_number"),
          UUID.fromString(rs.getString("station_id")),
          rs.getString("salary")
      );

      employee.setId(UUID.fromString(rs.getString("id")));
      return employee;
    };
  }

  @Override
  protected Object[] getInsertValues(Employee entity) {
    return new Object[] {
        entity.getId().toString(),
        entity.getName(),
        entity.getPhoneNumber(),
        entity.getStationId().toString(),
        entity.getSalary()
    };
  }

  @Override
  protected Object[] getUpdateValues(Employee entity) {
    return new Object[] {
        entity.getName(),
        entity.getPhoneNumber(),
        entity.getStationId().toString(),
        entity.getSalary(),
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[] {
        "name",
        "phone_number",
        "station_id",
        "salary"
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }
}