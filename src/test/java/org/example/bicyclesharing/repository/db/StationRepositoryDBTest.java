package org.example.bicyclesharing.repository.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StationRepositoryDBTest extends AbstractRepositoryTest {

  private StationRepositoryDB repository;

  @BeforeEach
  void setUpRepository() {
    repository = new StationRepositoryDB(dataSource);
  }

  @Test
  void save_shouldInsertNewStation_whenValidData() {
    UUID bicycleId = UUID.randomUUID();
    Station station = Station.fromDatabase(UUID.randomUUID(), "Central Station",
        48.6208, 22.2879, List.of(bicycleId));

    repository.save(station);

    Optional<Station> loaded = repository.findById(station.getId());

    assertThat(loaded).isPresent();
    assertThat(loaded.get().getName()).isEqualTo("Central Station");
    assertThat(loaded.get().getBicyclesId()).containsExactly(bicycleId);
    assertThat(countRowsInTable("STATIONS")).isEqualTo(1);
  }

  @Test
  void findById_shouldReturnStation_whenExists() {
    Station station = Station.fromDatabase(UUID.randomUUID(), "Station A",
        10.0, 20.0, List.of());

    repository.save(station);

    Optional<Station> result = repository.findById(station.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Station A");
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    Optional<Station> result = repository.findById(UUID.randomUUID());

    assertThat(result).isEmpty();
  }

  @Test
  void getById_shouldReturnStation_whenExists() {
    Station station = Station.fromDatabase(UUID.randomUUID(), "Station B",
        11.0, 22.0, List.of());

    repository.save(station);

    Station result = repository.getById(station.getId());

    assertThat(result.getName()).isEqualTo("Station B");
  }

  @Test
  void findAll_shouldReturnAllStations_whenMultipleExist() {
    repository.save(Station.fromDatabase(UUID.randomUUID(), "A", 1.0, 1.0, List.of()));
    repository.save(Station.fromDatabase(UUID.randomUUID(), "B", 2.0, 2.0, List.of()));

    List<Station> result = repository.findAll();

    assertThat(result).hasSize(2);
    assertThat(result).extracting(Station::getName)
        .containsExactlyInAnyOrder("A", "B");
  }

  @Test
  void findAll_shouldReturnEmptyList_whenTableEmpty() {
    List<Station> result = repository.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void update_shouldModifyAllFields_whenStationExists() {
    UUID id = UUID.randomUUID();
    UUID bicycleId = UUID.randomUUID();

    repository.save(Station.fromDatabase(id, "Old Station",
        1.0, 2.0, List.of()));

    Station updated = Station.fromDatabase(id, "New Station",
        3.0, 4.0, List.of(bicycleId));

    repository.update(updated);

    Station loaded = repository.findById(id).orElseThrow();

    assertThat(loaded.getName()).isEqualTo("New Station");
    assertThat(loaded.getLatitude()).isEqualTo(3.0);
    assertThat(loaded.getLongitude()).isEqualTo(4.0);
    assertThat(loaded.getBicyclesId()).containsExactly(bicycleId);
  }

  @Test
  void deleteById_shouldRemoveStation_whenExists() {
    Station station = Station.fromDatabase(UUID.randomUUID(), "Delete Station",
        1.0, 1.0, List.of());

    repository.save(station);

    boolean deleted = repository.deleteById(station.getId());

    assertThat(deleted).isTrue();
    assertThat(repository.findById(station.getId())).isEmpty();
    assertThat(countRowsInTable("STATIONS")).isEqualTo(0);
  }

  @Test
  void deleteById_shouldReturnFalse_whenNotExists() {
    boolean deleted = repository.deleteById(UUID.randomUUID());

    assertThat(deleted).isFalse();
  }

  @Test
  void count_shouldReturnCorrectNumber_whenMultipleStationsExist() {
    repository.save(Station.fromDatabase(UUID.randomUUID(), "A", 1.0, 1.0, List.of()));
    repository.save(Station.fromDatabase(UUID.randomUUID(), "B", 2.0, 2.0, List.of()));

    assertThat(repository.count()).isEqualTo(2);
  }

  @Test
  void existsById_shouldReturnTrue_whenStationExists() {
    Station station = Station.fromDatabase(UUID.randomUUID(), "Exists Station",
        1.0, 1.0, List.of());

    repository.save(station);

    assertThat(repository.existsById(station.getId())).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenStationNotExists() {
    assertThat(repository.existsById(UUID.randomUUID())).isFalse();
  }

  @Test
  void findByFilters_shouldReturnMatchingStations_whenSearchProvided() {
    repository.save(Station.fromDatabase(UUID.randomUUID(), "Central Station", 1.0, 1.0, List.of()));
    repository.save(Station.fromDatabase(UUID.randomUUID(), "West Station", 2.0, 2.0, List.of()));

    List<Station> result = repository.findByFilters("Central");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Central Station");
  }
}