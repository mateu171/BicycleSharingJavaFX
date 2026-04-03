package org.example.bicyclesharing.services;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.ReservationRepository;

public class ReservationService extends BaseService<Reservation, UUID>{

  private ReservationRepository reservationRepository;
  @Override
  protected Repository<Reservation, UUID> getRepository() {
    return reservationRepository;
  }

  public List<Reservation> getByReservationStatus(ReservationStatus status)
  {
    return reservationRepository.findByReservationStatus(status);
  }
}
