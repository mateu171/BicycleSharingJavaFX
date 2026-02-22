package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;

public interface BicycleRepository extends Repository<Bicycle, UUID> {

  List<Bicycle> findByState(StateBicycle stateBicycle);

  List<Bicycle> findByType(TypeBicycle type);
}
