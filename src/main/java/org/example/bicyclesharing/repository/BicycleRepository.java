package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;

public interface BicycleRepository extends Repository<Bicycle, UUID> {

  List<Bicycle> findByFilters(String search, StateBicycle state);
  List<Bicycle> findByState(StateBicycle state);
}
