package org.example.bicyclesharing.repository.db;

import java.util.List;
import java.util.UUID;

import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.dto.LatestCustomerInfo;
import org.example.bicyclesharing.repository.CustomerRepository;
import org.springframework.jdbc.core.RowMapper;

public class CustomerRepositoryDB extends BaseRepositoryDB<Customer, UUID> implements CustomerRepository {

  @Override
  protected String getTableName() {
    return "customers";
  }

  @Override
  protected String getCreateTableSQL() {
    return """
      CREATE TABLE IF NOT EXISTS customers (
        id VARCHAR(36) PRIMARY KEY,
        full_name VARCHAR(255) NOT NULL,
        phone_number VARCHAR(50) NOT NULL,
        document_number VARCHAR(36),
        active_rent VARCHAR(36),
        active_reservation VARCHAR(36)
      )
    """;
  }

  @Override
  protected UUID getId(Customer e) {
    return e.getId();
  }

  @Override
  protected RowMapper<Customer> rowMapper() {
    return (rs, rowNum) -> Customer.fromDatabase(
        UUID.fromString(rs.getString("id")),
        rs.getString("full_name"),
        rs.getString("phone_number"),
        rs.getString("document_number"),
        rs.getString("active_rent") == null ? null : UUID.fromString(rs.getString("active_rent")),
        rs.getString("active_reservation") == null ? null : UUID.fromString(rs.getString("active_reservation"))
    );
  }

  @Override
  protected Object[] getInsertValues(Customer e) {
    return new Object[]{
        e.getId().toString(),
        e.getFullName(),
        e.getPhoneNumber(),
        e.getDocumentNumber(),
        e.getActiveRent() != null ? e.getActiveRent().toString() : null,
        e.getActiveReservation() != null ? e.getActiveReservation().toString() : null
    };
  }

  @Override
  protected Object[] getUpdateValues(Customer e) {
    return new Object[]{
        e.getFullName(),
        e.getPhoneNumber(),
        e.getDocumentNumber(),
        e.getActiveRent() != null ? e.getActiveRent().toString() : null,
        e.getActiveReservation() != null ? e.getActiveReservation().toString() : null,
        e.getId().toString()
    };
  }

  @Override
  protected String[] getUpdateColumns() {
    return new String[]{
        "full_name",
        "phone_number",
        "document_number",
        "active_rent",
        "active_reservation"
    };
  }

  @Override
  protected String getIdColumn() {
    return "id";
  }

  @Override
  public List<Customer> findByFilters(String search) {
    QueryData query = new QueryData("SELECT * FROM customers WHERE 1=1");

    query.addLikeCondition("full_name", search);
    query.addOrderBy("full_name","ASC");

    return jdbcTemplate.query(query.getSql(), rowMapper(), query.getParams());
  }

  @Override
  public LatestCustomerInfo getLatestCustomerInfo() {
    String sql = """
      SELECT
          full_name
      FROM CUSTOMERS
      ORDER BY full_name DESC
      LIMIT 1
      """;

    List<LatestCustomerInfo> result = jdbcTemplate.query(
        sql,
        (rs, rowNum) ->
            new LatestCustomerInfo(
                rs.getString("full_name")
            )
    );

    return result.isEmpty()
        ? null
        : result.getFirst();
  }
}