package org.example.bicyclesharing.repository.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BikeIssueRepositoryDBTest extends AbstractRepositoryTest {

  private BikeIssueRepositoryDB repository;

  @BeforeEach
  void setUpRepository() {
    repository = new BikeIssueRepositoryDB(dataSource);
  }

  @Test
  void save_shouldInsertNewBikeIssue_whenValidData() {
    BikeIssue issue = createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "Flat tire", IssueStatus.NEW);

    repository.save(issue);

    Optional<BikeIssue> loaded = repository.findById(issue.getId());

    assertThat(loaded).isPresent();
    assertThat(loaded.get().getProblemType()).isEqualTo("Flat tire");
    assertThat(loaded.get().getStatus()).isEqualTo(IssueStatus.NEW);
    assertThat(countRowsInTable("BIKE_ISSUES")).isEqualTo(1);
  }

  @Test
  void findById_shouldReturnBikeIssue_whenExists() {
    BikeIssue issue = createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "Broken chain", IssueStatus.NEW);

    repository.save(issue);

    Optional<BikeIssue> result = repository.findById(issue.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getProblemType()).isEqualTo("Broken chain");
  }

  @Test
  void findById_shouldReturnEmpty_whenNotExists() {
    Optional<BikeIssue> result = repository.findById(UUID.randomUUID());

    assertThat(result).isEmpty();
  }

  @Test
  void findAll_shouldReturnAllBikeIssues_whenMultipleExist() {
    repository.save(createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "A", IssueStatus.NEW));
    repository.save(createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "B", IssueStatus.IN_PROGRESS));

    List<BikeIssue> result = repository.findAll();

    assertThat(result).hasSize(2);
  }

  @Test
  void findAll_shouldReturnEmptyList_whenTableEmpty() {
    List<BikeIssue> result = repository.findAll();

    assertThat(result).isEmpty();
  }

  @Test
  void update_shouldModifyAllFields_whenBikeIssueExists() {
    UUID id = UUID.randomUUID();

    repository.save(createIssue(id, UUID.randomUUID(), UUID.randomUUID(),
        "Old problem", IssueStatus.NEW));

    BikeIssue updated = createIssue(id, UUID.randomUUID(), UUID.randomUUID(),
        "New problem", IssueStatus.RESOLVED);

    repository.update(updated);

    BikeIssue loaded = repository.findById(id).orElseThrow();

    assertThat(loaded.getProblemType()).isEqualTo("New problem");
    assertThat(loaded.getStatus()).isEqualTo(IssueStatus.RESOLVED);
  }

  @Test
  void deleteById_shouldRemoveBikeIssue_whenExists() {
    BikeIssue issue = createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "Delete", IssueStatus.NEW);

    repository.save(issue);

    boolean deleted = repository.deleteById(issue.getId());

    assertThat(deleted).isTrue();
    assertThat(repository.findById(issue.getId())).isEmpty();
    assertThat(countRowsInTable("BIKE_ISSUES")).isEqualTo(0);
  }

  @Test
  void deleteById_shouldReturnFalse_whenNotExists() {
    boolean deleted = repository.deleteById(UUID.randomUUID());

    assertThat(deleted).isFalse();
  }

  @Test
  void count_shouldReturnCorrectNumber_whenMultipleBikeIssuesExist() {
    repository.save(createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "A", IssueStatus.NEW));
    repository.save(createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "B", IssueStatus.NEW));

    assertThat(repository.count()).isEqualTo(2);
  }

  @Test
  void existsById_shouldReturnTrue_whenBikeIssueExists() {
    BikeIssue issue = createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "Exists", IssueStatus.NEW);

    repository.save(issue);

    assertThat(repository.existsById(issue.getId())).isTrue();
  }

  @Test
  void existsById_shouldReturnFalse_whenBikeIssueNotExists() {
    assertThat(repository.existsById(UUID.randomUUID())).isFalse();
  }

  @Test
  void findByStatus_shouldReturnMatchingIssues_whenStatusExists() {
    repository.save(createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "A", IssueStatus.NEW));
    repository.save(createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "B", IssueStatus.RESOLVED));

    List<BikeIssue> result = repository.findByStatus(IssueStatus.NEW);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStatus()).isEqualTo(IssueStatus.NEW);
  }

  @Test
  void findByBicycleId_shouldReturnMatchingIssues_whenBicycleExists() {
    UUID bicycleId = UUID.randomUUID();

    repository.save(createIssue(UUID.randomUUID(), UUID.randomUUID(), bicycleId,
        "A", IssueStatus.NEW));
    repository.save(createIssue(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "B", IssueStatus.NEW));

    List<BikeIssue> result = repository.findByBicycleId(bicycleId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getBicycleId()).isEqualTo(bicycleId);
  }

  private BikeIssue createIssue(UUID id, UUID rentalId, UUID bicycleId,
      String problemType, IssueStatus status) {
    return BikeIssue.fromDatabase(
        id,
        rentalId,
        bicycleId,
        problemType,
        "Comment",
        true,
        LocalDateTime.now(),
        status
    );
  }
}