package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Rental;

public interface RentalRepository extends Repository<Rental, UUID> {

  List<Rental> findByUserId(UUID id);
}
