package org.example.bicyclesharing.repository.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomerRepositoryDBTest extends AbstractRepositoryTest {

  private CustomerRepositoryDB repository;

  @BeforeEach
  void setUpRepository() {
    repository = new CustomerRepositoryDB(dataSource);
  }

  @Test
  void save_shouldInsertNewCustomer_whenValidData() {
    Customer customer = Customer.fromDatabase(UUID.randomUUID(), "John Doe",
        "+380991112233", "AB123456", null, null);

    repository.save(customer);

    Optional<Customer> loaded = repository.findById(customer.getId());

    assertThat(loaded).isPresent();
    assertThat(loaded.get().getFullName()).isEqualTo("John Doe");
    assertThat(loaded.get().getPhoneNumber()).isEqualTo("+380991112233");
    assertThat(countRowsInTable("CUSTOMERS")).isEqualTo(1);
  }

  @Test
  void findById_shouldReturnCustomer_whenExists() {
    Customer customer = Customer.fromDatabase(UUID.randomUUID(), "Alice Smith",
        "+380991112244", "CD123456", null, null);

    repository.save(customer);

    Optional<Customer> result = repository.findById(customer.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getFullName()).isEqualTo("Alice Smith");
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    Optional<Customer> result = repository.findById(UUID.randomUUID());

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllCustomers_whenMultipleExist() {
    repository.save(Customer.fromDatabase(UUID.randomUUID(), "Customer A",
        "+380991111111", "A1", null, null));
    repository.save(Customer.fromDatabase(UUID.randomUUID(), "Customer B",
        "+380992222222", "B1", null, null));

    List<Customer> result = repository.findAll();

    assertThat(result).hasSize(2);
    assertThat(result).extracting(Customer::getFullName)
        .containsExactlyInAnyOrder("Customer A", "Customer B");
  }

  @Test
  void findAll_shouldReturnEmptyList_whenTableEmpty() {
    List<Customer> result = repository.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void update_shouldModifyAllFields_whenCustomerExists() {
    UUID id = UUID.randomUUID();

    repository.save(Customer.fromDatabase(id, "Old Name",
        "+380991111111", "OLD", null, null));

    Customer updated = Customer.fromDatabase(id, "New Name",
        "+380992222222", "NEW", UUID.randomUUID(), UUID.randomUUID());

    repository.update(updated);

    Customer loaded = repository.findById(id).orElseThrow();

    assertThat(loaded.getFullName()).isEqualTo("New Name");
    assertThat(loaded.getPhoneNumber()).isEqualTo("+380992222222");
    assertThat(loaded.getDocumentNumber()).isEqualTo("NEW");
    assertThat(loaded.getActiveRent()).isEqualTo(updated.getActiveRent());
    assertThat(loaded.getActiveReservation()).isEqualTo(updated.getActiveReservation());
  }

  @Test
  void deleteById_shouldRemoveCustomer_whenExists() {
    Customer customer = Customer.fromDatabase(UUID.randomUUID(), "Delete Me",
        "+380991112233", "DEL", null, null);

    repository.save(customer);

    boolean deleted = repository.deleteById(customer.getId());

    assertThat(deleted).isTrue();
    assertThat(repository.findById(customer.getId())).isEmpty();
    assertThat(countRowsInTable("CUSTOMERS")).isEqualTo(0);
  }

  @Test
  void deleteById_shouldReturnFalse_whenNotExists() {
    boolean deleted = repository.deleteById(UUID.randomUUID());

    assertThat(deleted).isFalse();
  }

  @Test
  void count_shouldReturnCorrectNumber_whenMultipleCustomersExist() {
    repository.save(Customer.fromDatabase(UUID.randomUUID(), "A", "+380991111111", "A", null, null));
    repository.save(Customer.fromDatabase(UUID.randomUUID(), "B", "+380992222222", "B", null, null));

    assertThat(repository.count()).isEqualTo(2);
  }

  @Test
  void existsById_shouldReturnTrue_whenCustomerExists() {
    Customer customer = Customer.fromDatabase(UUID.randomUUID(), "Exists",
        "+380991112233", "EX", null, null);

    repository.save(customer);

    assertThat(repository.existsById(customer.getId())).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenCustomerNotExists() {
    assertThat(repository.existsById(UUID.randomUUID())).isFalse();
  }

  @Test
  void findByFilters_shouldReturnMatchingCustomers_whenSearchProvided() {
    repository.save(Customer.fromDatabase(UUID.randomUUID(), "John Doe",
        "+380991111111", "A", null, null));
    repository.save(Customer.fromDatabase(UUID.randomUUID(), "Alice Smith",
        "+380992222222", "B", null, null));

    List<Customer> result = repository.findByFilters("John");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getFullName()).isEqualTo("John Doe");
  }
}