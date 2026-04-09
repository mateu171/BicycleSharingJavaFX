package org.example.bicyclesharing.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.repository.BicycleRepository;
import org.example.bicyclesharing.repository.Repository;

public class BicycleService extends BaseService<Bicycle, UUID> {

  private final BicycleRepository repository;

  public BicycleService(BicycleRepository repository) {
    this.repository = repository;
  }

  @Override
  protected Repository<Bicycle, UUID> getRepository() {
    return repository;
  }

  public Optional<Bicycle> getById(UUID id) {
    return repository.findById(id);
  }

  public List<Bicycle> getByFilters(String search, StateBicycle stateBicycle)
  {
    return repository.findByFilters(search,stateBicycle);
  }

  public List<Bicycle> getAvailable() {
    return repository.findByState(StateBicycle.AVAILABLE);
  }
}

