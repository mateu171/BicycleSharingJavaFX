package org.example.bicyclesharing.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.repository.BicycleRepository;
import org.example.bicyclesharing.repository.CustomerRepository;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.ReservationRepository;
import org.example.bicyclesharing.repository.db.ReservationRepositoryDB;
import org.example.bicyclesharing.util.LocalizationManager;

public class ReservationService extends BaseService<Reservation, UUID>{

  private ReservationRepository reservationRepository;
  private BicycleRepository bicycleRepository;
  private CustomerRepository customerRepository;

  public ReservationService(ReservationRepository reservationRepository,
      BicycleRepository bicycleRepository, CustomerRepository customerRepository) {
    this.reservationRepository = reservationRepository;
    this.bicycleRepository = bicycleRepository;
    this.customerRepository = customerRepository;
  }

  @Override
  protected Repository<Reservation, UUID> getRepository() {
    return reservationRepository;
  }

  public List<Reservation> findByFilters(String search, ReservationStatus status) {
    return reservationRepository.findByFilters(search, status);
  }

  public void updateStatuses() {

    executeInTransaction(() -> {
      List<Reservation> toUpdate =
          reservationRepository.findNotIssuedButStarted(LocalDateTime.now());

      for (Reservation r : toUpdate) {
        r.setStatus(ReservationStatus.ISSUED);
        reservationRepository.update(r);
      }
    });
  }

  public void validateCanCreateReservation() {
    boolean hasAvailableBicycle = bicycleRepository.countByState(StateBicycle.AVAILABLE) > 0;

    boolean hasCustomers = customerRepository.count() > 0;
    if (!hasAvailableBicycle) {
      throw new BusinessException(
          "error.reservation.no.available.bicycles"
      );
    } else if (!hasCustomers) {
      throw new BusinessException("error.reservation.no.customers");
    }
  }
}
