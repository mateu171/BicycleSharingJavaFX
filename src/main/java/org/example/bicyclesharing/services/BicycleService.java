package org.example.bicyclesharing.services;

import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
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
}

