package org.example.bicyclesharing.repository.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BicycleRepositoryDBTest extends AbstractRepositoryTest {

  private BicycleRepositoryDB repository;

  @BeforeEach
  void setUpRepository() {
    repository = new BicycleRepositoryDB(dataSource);
  }

  @Test
  void save_shouldInsertNewBicycle_whenValidData() {
    Bicycle bicycle = Bicycle.fromDatabase(UUID.randomUUID(), "Cube Aim", TypeBicycle.MOUNTAIN,
        StateBicycle.AVAILABLE, 2.5, null, "bike.png");

    repository.save(bicycle);

    Optional<Bicycle> loaded = repository.findById(bicycle.getId());

    assertThat(loaded).isPresent();
    assertThat(loaded.get().getModel()).isEqualTo("Cube Aim");
    assertThat(loaded.get().getTypeBicycle()).isEqualTo(TypeBicycle.MOUNTAIN);
    assertThat(loaded.get().getState()).isEqualTo(StateBicycle.AVAILABLE);
    assertThat(loaded.get().getPricePerMinute()).isEqualTo(2.5);
    assertThat(countRowsInTable("BICYCLES")).isEqualTo(1);
  }

  @Test
  void findById_shouldReturnBicycle_whenExists() {
    Bicycle bicycle = Bicycle.fromDatabase(UUID.randomUUID(), "Trek", TypeBicycle.ROAD,
        StateBicycle.AVAILABLE, 3.0, null, null);

    repository.save(bicycle);

    Optional<Bicycle> result = repository.findById(bicycle.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getModel()).isEqualTo("Trek");
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    Optional<Bicycle> result = repository.findById(UUID.randomUUID());

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllBicycles_whenMultipleExist() {
    repository.save(Bicycle.fromDatabase(UUID.randomUUID(), "Cube", TypeBicycle.MOUNTAIN,
        StateBicycle.AVAILABLE, 2.5, null, null));
    repository.save(Bicycle.fromDatabase(UUID.randomUUID(), "Trek", TypeBicycle.ROAD,
        StateBicycle.RENTED, 3.0, null, null));

    List<Bicycle> result = repository.findAll();

    assertThat(result).hasSize(2);
    assertThat(result).extracting(Bicycle::getModel)
        .containsExactlyInAnyOrder("Cube", "Trek");
  }

  @Test
  void findAll_shouldReturnEmptyList_whenTableEmpty() {
    List<Bicycle> result = repository.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void update_shouldModifyAllFields_whenBicycleExists() {
    UUID id = UUID.randomUUID();

    repository.save(Bicycle.fromDatabase(id, "Old", TypeBicycle.URBAN,
        StateBicycle.AVAILABLE, 1.0, null, null));

    Bicycle updated = Bicycle.fromDatabase(id, "New", TypeBicycle.ELECTRIC,
        StateBicycle.ON_MAINTENANCE, 4.0, null, "new.png");

    repository.update(updated);

    Bicycle loaded = repository.findById(id).orElseThrow();

    assertThat(loaded.getModel()).isEqualTo("New");
    assertThat(loaded.getTypeBicycle()).isEqualTo(TypeBicycle.ELECTRIC);
    assertThat(loaded.getState()).isEqualTo(StateBicycle.ON_MAINTENANCE);
    assertThat(loaded.getPricePerMinute()).isEqualTo(4.0);
    assertThat(loaded.getImagePath()).isEqualTo("new.png");
  }

  @Test
  void deleteById_shouldRemoveBicycle_whenExists() {
    Bicycle bicycle = Bicycle.fromDatabase(UUID.randomUUID(), "Delete Bike", TypeBicycle.ROAD,
        StateBicycle.AVAILABLE, 2.0, null, null);

    repository.save(bicycle);

    boolean deleted = repository.deleteById(bicycle.getId());

    assertThat(deleted).isTrue();
    assertThat(repository.findById(bicycle.getId())).isEmpty();
    assertThat(countRowsInTable("BICYCLES")).isEqualTo(0);
  }

  @Test
  void deleteById_shouldReturnFalse_whenNotExists() {
    boolean deleted = repository.deleteById(UUID.randomUUID());

    assertThat(deleted).isFalse();
  }

  @Test
  void count_shouldReturnCorrectNumber_whenMultipleBicyclesExist() {
    repository.save(Bicycle.fromDatabase(UUID.randomUUID(), "A", TypeBicycle.URBAN,
        StateBicycle.AVAILABLE, 1.0, null, null));
    repository.save(Bicycle.fromDatabase(UUID.randomUUID(), "B", TypeBicycle.ROAD,
        StateBicycle.AVAILABLE, 1.0, null, null));

    long count = repository.count();

    assertThat(count).isEqualTo(2);
  }

  @Test
  void existsById_shouldReturnTrue_whenBicycleExists() {
    Bicycle bicycle = Bicycle.fromDatabase(UUID.randomUUID(), "Exists", TypeBicycle.URBAN,
        StateBicycle.AVAILABLE, 1.0, null, null);

    repository.save(bicycle);

    assertThat(repository.existsById(bicycle.getId())).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenBicycleNotExists() {
    assertThat(repository.existsById(UUID.randomUUID())).isFalse();
  }

  @Test
  void findByState_shouldReturnMatchingBicycles_whenStateExists() {
    repository.save(Bicycle.fromDatabase(UUID.randomUUID(), "Available Bike", TypeBicycle.URBAN,
        StateBicycle.AVAILABLE, 1.0, null, null));
    repository.save(Bicycle.fromDatabase(UUID.randomUUID(), "Rented Bike", TypeBicycle.ROAD,
        StateBicycle.RENTED, 1.0, null, null));

    List<Bicycle> result = repository.findByState(StateBicycle.AVAILABLE);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getModel()).isEqualTo("Available Bike");
  }

  @Test
  void findByFilters_shouldReturnMatchingBicycles_whenSearchAndStateProvided() {
    repository.save(Bicycle.fromDatabase(UUID.randomUUID(), "Cube Aim", TypeBicycle.MOUNTAIN,
        StateBicycle.AVAILABLE, 2.5, null, null));
    repository.save(Bicycle.fromDatabase(UUID.randomUUID(), "Trek Road", TypeBicycle.ROAD,
        StateBicycle.RENTED, 3.0, null, null));

    List<Bicycle> result = repository.findByFilters("Cube", StateBicycle.AVAILABLE);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getModel()).isEqualTo("Cube Aim");
  }
}