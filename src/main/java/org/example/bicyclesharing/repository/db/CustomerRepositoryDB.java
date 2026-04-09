package org.example.bicyclesharing.repository.db;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.repository.CustomerRepository;
import org.springframework.jdbc.core.RowMapper;

public class CustomerRepositoryDB extends BaseRepositoryDB<Customer, UUID> implements
    CustomerRepository {

  @Override
  protected String getCreateTableSQL() {
    return "CREATE TABLE IF NOT EXISTS CUSTOMERS (" +
        "id VARCHAR(36) PRIMARY KEY," +
        "full_name VARCHAR(255) NOT NULL," +
        "phone_number VARCHAR(50) NOT NULL," +
        "document_number VARCHAR(36)," +
        "active_rent VARCHAR(36)" +
        ")";
  }

  @Override
  protected String getTableName() {
    return "CUSTOMERS";
  }

  @Override
  protected UUID getId(Customer entity) {
    return entity.getId();
  }

  @Override
  protected RowMapper<Customer> rowMapper() {
    return (rs, rowNum) -> Customer.fromDatabase(
        UUID.fromString(rs.getString("id")),
        rs.getString("full_name"),
        rs.getString("phone_number"),
        rs.getString("document_number"),
        rs.getString("active_rent") == null ? null : UUID.fromString(rs.getString("active_rent"))
    );
  }

  @Override
  protected Object[] getInsertValues(Customer entity) {
    return new Object[] {
        entity.getId().toString(),
        entity.getFullName(),
        entity.getPhoneNumber(),
        entity.getDocumentNumber(),
        entity.getActiveRent() != null ? entity.getActiveRent().toString() : null
    };
  }

  @Override
  protected Object[] getUpdateValues(Customer entity) {
    return new Object[] {
        entity.getFullName(),
        entity.getPhoneNumber(),
        entity.getDocumentNumber(),
        entity.getActiveRent() != null ? entity.getActiveRent().toString() : null,
        entity.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[] {
        "full_name",
        "phone_number",
        "document_number",
        "active_rent"
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  public List<Customer> findByFilters(String search) {
    QueryData query = new QueryData("SELECT * FROM CUSTOMERS WHERE 1=1");

    query.addLikeCondition("full_name", search);
    query.addOrderBy("full_name ASC");

    return jdbcTemplate.query(query.getSql(), rowMapper(), query.getParams());
  }
}
