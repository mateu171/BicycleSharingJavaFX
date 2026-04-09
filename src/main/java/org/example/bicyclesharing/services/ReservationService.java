package org.example.bicyclesharing.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.ReservationRepository;
import org.example.bicyclesharing.repository.db.ReservationRepositoryDB;

public class ReservationService extends BaseService<Reservation, UUID>{

  private ReservationRepository reservationRepository;

  public ReservationService(ReservationRepositoryDB reservationRepositoryDB) {
    this.reservationRepository = reservationRepositoryDB;
  }

  @Override
  protected Repository<Reservation, UUID> getRepository() {
    return reservationRepository;
  }

  public List<Reservation> findByFilters(String search, ReservationStatus status) {
    return reservationRepository.findByFilters(search, status);
  }

  public void updateStatuses() {
    List<Reservation> toUpdate =
        reservationRepository.findNotIssuedButStarted(LocalDateTime.now());

    for (Reservation r : toUpdate) {
      r.setStatus(ReservationStatus.ISSUED);
      reservationRepository.update(r);
    }
  }
}
