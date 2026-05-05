package org.example.bicyclesharing.repository.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.domain.enums.MaintenanceAction;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MaintenanceRecordRepositoryDBTest extends AbstractRepositoryTest {

  private MaintenanceRecordRepositoryDB repository;

  @BeforeEach
  void setUpRepository() {
    repository = new MaintenanceRecordRepositoryDB(dataSource);
  }

  @Test
  void save_shouldInsertNewMaintenanceRecord_whenValidData() {
    MaintenanceRecord record = createRecord(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), MaintenanceType.REPAIR, MaintenanceAction.RETURN_TO_AVAILABLE);

    repository.save(record);

    Optional<MaintenanceRecord> loaded = repository.findById(record.getId());

    assertThat(loaded).isPresent();
    assertThat(loaded.get().getType()).isEqualTo(MaintenanceType.REPAIR);
    assertThat(loaded.get().getAction()).isEqualTo(MaintenanceAction.RETURN_TO_AVAILABLE);
    assertThat(countRowsInTable("MAINTENANCE_RECORDS")).isEqualTo(1);
  }

  @Test
  void findById_shouldReturnMaintenanceRecord_whenExists() {
    MaintenanceRecord record = createRecord(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), MaintenanceType.REPAIR, MaintenanceAction.RETURN_TO_AVAILABLE);

    repository.save(record);

    Optional<MaintenanceRecord> result = repository.findById(record.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(record.getId());
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    Optional<MaintenanceRecord> result = repository.findById(UUID.randomUUID());

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllMaintenanceRecords_whenMultipleExist() {
    repository.save(createRecord(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        MaintenanceType.REPAIR, MaintenanceAction.RETURN_TO_AVAILABLE));
    repository.save(createRecord(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        MaintenanceType.SERVICE, MaintenanceAction.RETURN_TO_AVAILABLE));

    List<MaintenanceRecord> result = repository.findAll();

    assertThat(result).hasSize(2);
  }

  @Test
  void findAll_shouldReturnEmptyList_whenTableEmpty() {
    List<MaintenanceRecord> result = repository.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void update_shouldModifyAllFields_whenMaintenanceRecordExists() {
    UUID id = UUID.randomUUID();

    repository.save(createRecord(id, UUID.randomUUID(), UUID.randomUUID(),
        MaintenanceType.REPAIR, MaintenanceAction.RETURN_TO_AVAILABLE));

    MaintenanceRecord updated = createRecord(id, UUID.randomUUID(), UUID.randomUUID(),
        MaintenanceType.SERVICE, MaintenanceAction.WRITE_OFF);

    repository.update(updated);

    MaintenanceRecord loaded = repository.findById(id).orElseThrow();

    assertThat(loaded.getBicycleId()).isEqualTo(updated.getBicycleId());
    assertThat(loaded.getMechanicId()).isEqualTo(updated.getMechanicId());
    assertThat(loaded.getType()).isEqualTo(MaintenanceType.SERVICE);
    assertThat(loaded.getAction()).isEqualTo(MaintenanceAction.WRITE_OFF);
  }

  @Test
  void deleteById_shouldRemoveMaintenanceRecord_whenExists() {
    MaintenanceRecord record = createRecord(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), MaintenanceType.REPAIR, MaintenanceAction.RETURN_TO_AVAILABLE);

    repository.save(record);

    boolean deleted = repository.deleteById(record.getId());

    assertThat(deleted).isTrue();
    assertThat(repository.findById(record.getId())).isEmpty();
    assertThat(countRowsInTable("MAINTENANCE_RECORDS")).isEqualTo(0);
  }

  @Test
  void deleteById_shouldReturnFalse_whenNotExists() {
    boolean deleted = repository.deleteById(UUID.randomUUID());

    assertThat(deleted).isFalse();
  }

  @Test
  void count_shouldReturnCorrectNumber_whenMultipleMaintenanceRecordsExist() {
    repository.save(createRecord(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        MaintenanceType.REPAIR, MaintenanceAction.RETURN_TO_AVAILABLE));
    repository.save(createRecord(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        MaintenanceType.SERVICE, MaintenanceAction.RETURN_TO_AVAILABLE));

    assertThat(repository.count()).isEqualTo(2);
  }

  @Test
  void existsById_shouldReturnTrue_whenMaintenanceRecordExists() {
    MaintenanceRecord record = createRecord(UUID.randomUUID(), UUID.randomUUID(),
        UUID.randomUUID(), MaintenanceType.REPAIR, MaintenanceAction.RETURN_TO_AVAILABLE);

    repository.save(record);

    assertThat(repository.existsById(record.getId())).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenMaintenanceRecordNotExists() {
    assertThat(repository.existsById(UUID.randomUUID())).isFalse();
  }

  @Test
  void findByBicycleId_shouldReturnMatchingRecords_whenBicycleExists() {
    UUID bicycleId = UUID.randomUUID();

    repository.save(createRecord(UUID.randomUUID(), bicycleId, UUID.randomUUID(),
        MaintenanceType.REPAIR, MaintenanceAction.RETURN_TO_AVAILABLE));
    repository.save(createRecord(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        MaintenanceType.SERVICE, MaintenanceAction.RETURN_TO_AVAILABLE));

    List<MaintenanceRecord> result = repository.findByBicycleId(bicycleId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getBicycleId()).isEqualTo(bicycleId);
  }

  private MaintenanceRecord createRecord(UUID id, UUID bicycleId, UUID mechanicId,
      MaintenanceType type, MaintenanceAction action) {
    return MaintenanceRecord.fromDatabase(
        id,
        bicycleId,
        mechanicId,
        type,
        "Description",
        "Result",
        action,
        LocalDateTime.now()
    );
  }
}