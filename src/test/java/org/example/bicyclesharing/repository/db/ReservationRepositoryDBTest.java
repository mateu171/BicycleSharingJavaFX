package org.example.bicyclesharing.repository.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.enums.DocumentType;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReservationRepositoryDBTest extends AbstractRepositoryTest {

  private ReservationRepositoryDB repository;
  private CustomerRepositoryDB customerRepository;

  @BeforeEach
  void setUpRepository() {
    customerRepository = new CustomerRepositoryDB(dataSource);
    repository = new ReservationRepositoryDB(dataSource);
  }

  @Test
  void save_shouldInsertNewReservation_whenValidData() {
    Reservation reservation = createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now());

    repository.save(reservation);

    Optional<Reservation> loaded = repository.findById(reservation.getId());

    assertThat(loaded).isPresent();
    assertThat(loaded.get().getStatus()).isEqualTo(ReservationStatus.NEW);
    assertThat(countRowsInTable("RESERVATIONS")).isEqualTo(1);
  }

  @Test
  void findById_shouldReturnReservation_whenExists() {
    Reservation reservation = createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now());

    repository.save(reservation);

    Optional<Reservation> result = repository.findById(reservation.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(reservation.getId());
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    Optional<Reservation> result = repository.findById(UUID.randomUUID());

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllReservations_whenMultipleExist() {
    repository.save(createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now()));
    repository.save(createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now()));

    List<Reservation> result = repository.findAll();

    assertThat(result).hasSize(2);
  }

  @Test
  void findAll_shouldReturnEmptyList_whenTableEmpty() {
    List<Reservation> result = repository.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void update_shouldModifyAllFields_whenReservationExists() {
    UUID id = UUID.randomUUID();

    repository.save(createReservation(id, UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now()));

    Reservation updated = createReservation(id, UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), ReservationStatus.CANCELLED, LocalDateTime.now().plusDays(1));

    repository.update(updated);

    Reservation loaded = repository.findById(id).orElseThrow();

    assertThat(loaded.getCustomerId()).isEqualTo(updated.getCustomerId());
    assertThat(loaded.getBicycleId()).isEqualTo(updated.getBicycleId());
    assertThat(loaded.getManagerId()).isEqualTo(updated.getManagerId());
    assertThat(loaded.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
  }

  @Test
  void deleteById_shouldRemoveReservation_whenExists() {
    Reservation reservation = createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now());

    repository.save(reservation);

    boolean deleted = repository.deleteById(reservation.getId());

    assertThat(deleted).isTrue();
    assertThat(repository.findById(reservation.getId())).isEmpty();
    assertThat(countRowsInTable("RESERVATIONS")).isEqualTo(0);
  }

  @Test
  void deleteById_shouldReturnFalse_whenNotExists() {
    boolean deleted = repository.deleteById(UUID.randomUUID());

    assertThat(deleted).isFalse();
  }

  @Test
  void count_shouldReturnCorrectNumber_whenMultipleReservationsExist() {
    repository.save(createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now()));
    repository.save(createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now()));

    assertThat(repository.count()).isEqualTo(2);
  }

  @Test
  void existsById_shouldReturnTrue_whenReservationExists() {
    Reservation reservation = createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now());

    repository.save(reservation);

    assertThat(repository.existsById(reservation.getId())).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenReservationNotExists() {
    assertThat(repository.existsById(UUID.randomUUID())).isFalse();
  }

  @Test
  void findByFilters_shouldReturnMatchingReservations_whenSearchAndStatusProvided() {
    UUID customerId = UUID.randomUUID();

    customerRepository.save(Customer.fromDatabase(customerId, "John Customer",
        "+380991112233", "DOC", null, null));

    Reservation reservation = createReservation(UUID.randomUUID(), customerId,
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now());

    repository.save(reservation);

    List<Reservation> result = repository.findByFilters("John", ReservationStatus.NEW);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(reservation.getId());
  }

  @Test
  void findNotIssuedButStarted_shouldReturnReservations_whenStartTimePassedAndStatusNew() {
    Reservation started = createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now().minusHours(1));

    Reservation future = createReservation(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), UUID.randomUUID(), ReservationStatus.NEW, LocalDateTime.now().plusDays(1));

    repository.save(started);
    repository.save(future);

    List<Reservation> result = repository.findNotIssuedButStarted(LocalDateTime.now());

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(started.getId());
  }

  private Reservation createReservation(UUID id, UUID customerId, UUID bicycleId,
      UUID managerId, ReservationStatus status, LocalDateTime startTime) {
    return Reservation.fromDatabase(
        id,
        customerId,
        bicycleId,
        managerId,
        startTime,
        startTime.plusHours(2),
        DocumentType.PASSPORT,
        "AB123456",
        100.0,
        true,
        false,
        status
    );
  }
}