package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.enums.RentalStatus;

public interface RentalRepository extends Repository<Rental, UUID> {

  List<Rental> findByUserId(UUID id);

  List<Rental> findByStationId(UUID id);

  List<Rental> findByRentalStatus(RentalStatus rentalStatus);
}
