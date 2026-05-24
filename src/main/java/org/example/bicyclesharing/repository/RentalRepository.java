package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.dto.LatestRentalInfo;

public interface RentalRepository extends Repository<Rental, UUID> {

  List<Rental> findByCustomerId(UUID id);
  List<Rental> findByFilters(String search);
  long countActiveRentals();
  LatestRentalInfo getLatestRentalInfo();
}
