package org.example.bicyclesharing.services;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
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

  public Bicycle getById(UUID id) {
    return repository.findById(id).orElse(null);
  }

  public List<Bicycle> getByState(StateBicycle stateBicycle) {
    return repository.findByState(stateBicycle);
  }

  public List<Bicycle> getByType(TypeBicycle typeBicycle) {
    return repository.findByType(typeBicycle);
  }
}

