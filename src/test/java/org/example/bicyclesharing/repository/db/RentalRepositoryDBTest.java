package org.example.bicyclesharing.repository.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RentalRepositoryDBTest extends AbstractRepositoryTest {

  private RentalRepositoryDB repository;
  private CustomerRepositoryDB customerRepository;
  private BicycleRepositoryDB bicycleRepository;

  @BeforeEach
  void setUpRepository() {
    customerRepository = new CustomerRepositoryDB(dataSource);
    bicycleRepository = new BicycleRepositoryDB(dataSource);
    repository = new RentalRepositoryDB(dataSource);
  }

  @Test
  void save_shouldInsertNewRental_whenValidData() {
    Rental rental = createRental(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0);

    repository.save(rental);

    Optional<Rental> loaded = repository.findById(rental.getId());

    assertThat(loaded).isPresent();
    assertThat(loaded.get().getCustomerId()).isEqualTo(rental.getCustomerId());
    assertThat(loaded.get().getBicycleId()).isEqualTo(rental.getBicycleId());
    assertThat(countRowsInTable("RENTALS")).isEqualTo(1);
  }

  @Test
  void findById_shouldReturnRental_whenExists() {
    Rental rental = createRental(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0);

    repository.save(rental);

    Optional<Rental> result = repository.findById(rental.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(rental.getId());
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    Optional<Rental> result = repository.findById(UUID.randomUUID());

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllRentals_whenMultipleExist() {
    repository.save(createRental(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0));
    repository.save(createRental(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), LocalDateTime.now().plusHours(1), 100.0));

    List<Rental> result = repository.findAll();

    assertThat(result).hasSize(2);
  }

  @Test
  void findAll_shouldReturnEmptyList_whenTableEmpty() {
    List<Rental> result = repository.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void update_shouldModifyAllFields_whenRentalExists() {
    UUID id = UUID.randomUUID();

    Rental rental = createRental(id, UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0);

    repository.save(rental);

    LocalDateTime end = LocalDateTime.now().plusHours(2);

    Rental updated = createRental(id, UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now().minusHours(1), end, 150.0);

    repository.update(updated);

    Rental loaded = repository.findById(id).orElseThrow();

    assertThat(loaded.getCustomerId()).isEqualTo(updated.getCustomerId());
    assertThat(loaded.getBicycleId()).isEqualTo(updated.getBicycleId());
    assertThat(loaded.getEnd()).isNotNull();
    assertThat(loaded.getTotalCost()).isEqualTo(150.0);
  }

  @Test
  void deleteById_shouldRemoveRental_whenExists() {
    Rental rental = createRental(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0);

    repository.save(rental);

    boolean deleted = repository.deleteById(rental.getId());

    assertThat(deleted).isTrue();
    assertThat(repository.findById(rental.getId())).isEmpty();
    assertThat(countRowsInTable("RENTALS")).isEqualTo(0);
  }

  @Test
  void deleteById_shouldReturnFalse_whenNotExists() {
    boolean deleted = repository.deleteById(UUID.randomUUID());

    assertThat(deleted).isFalse();
  }

  @Test
  void count_shouldReturnCorrectNumber_whenMultipleRentalsExist() {
    repository.save(createRental(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0));
    repository.save(createRental(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0));

    assertThat(repository.count()).isEqualTo(2);
  }

  @Test
  void existsById_shouldReturnTrue_whenRentalExists() {
    Rental rental = createRental(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0);

    repository.save(rental);

    assertThat(repository.existsById(rental.getId())).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenRentalNotExists() {
    assertThat(repository.existsById(UUID.randomUUID())).isFalse();
  }

  @Test
  void findByCustomerId_shouldReturnMatchingRentals_whenCustomerExists() {
    UUID customerId = UUID.randomUUID();

    repository.save(createRental(UUID.randomUUID(), customerId, UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0));
    repository.save(createRental(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        LocalDateTime.now(), null, 0.0));

    List<Rental> result = repository.findByCustomerId(customerId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getCustomerId()).isEqualTo(customerId);
  }

  @Test
  void findActiveByFilters_shouldReturnRentals_whenSearchMatchesCustomerOrBicycle() {
    UUID customerId = UUID.randomUUID();
    UUID bicycleId = UUID.randomUUID();

    customerRepository.save(Customer.fromDatabase(customerId, "John Customer",
        "+380991112233", "DOC", null, null));

    bicycleRepository.save(Bicycle.fromDatabase(bicycleId, "Cube Bike",
        TypeBicycle.MOUNTAIN, StateBicycle.RENTED, 2.0, null, null));

    Rental activeRental = createRental(UUID.randomUUID(), customerId, bicycleId,
        LocalDateTime.now(), null, 0.0);

    repository.save(activeRental);

    List<Rental> result = repository.findByFilters("John");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(activeRental.getId());
  }

  private Rental createRental(UUID id, UUID customerId, UUID bicycleId,
      LocalDateTime start, LocalDateTime end, double totalCost) {
    Rental rental = new Rental();
    rental.setId(id);
    rental.setCustomerId(customerId);
    rental.setBicycleId(bicycleId);
    rental.setStart(start);
    rental.setEnd(end);
    rental.setTotalCost(totalCost);
    return rental;
  }
}