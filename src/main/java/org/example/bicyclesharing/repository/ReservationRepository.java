package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.enums.ReservationStatus;

public interface ReservationRepository extends Repository<Reservation, UUID>{

  List<Reservation> findByFilters(String search, ReservationStatus status);

}
