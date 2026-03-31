package org.example.bicyclesharing.services;


import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.repository.BikeIssueRepository;
import org.example.bicyclesharing.repository.Repository;

public class BikeIssueService extends BaseService<BikeIssue, UUID> {

  private final BikeIssueRepository repository;

  public BikeIssueService(BikeIssueRepository repository) {
    this.repository = repository;
  }

  @Override
  protected Repository<BikeIssue, UUID> getRepository() {
    return repository;
  }

  public List<BikeIssue> getByStatus(IssueStatus status) {
    return repository.findByStatus(status);
  }

  public List<BikeIssue> getByBicycleId(UUID bicycleId) {
    return repository.findByBicycleId(bicycleId);
  }
}