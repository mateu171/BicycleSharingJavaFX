package org.example.bicyclesharing.services;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.StationRepository;

public class StationService extends BaseService<Station, UUID>{

  private final StationRepository repository;

  public StationService(StationRepository repository) {
    this.repository = repository;
  }

  @Override
  protected Repository<Station, UUID> getRepository() {
    return repository;
  }

  public  Station getById(UUID id)
  {
    return repository.getById(id);
  }

  public List<Station> findByFilters(String search) {
    return repository.findByFilters(search);
  }
}
