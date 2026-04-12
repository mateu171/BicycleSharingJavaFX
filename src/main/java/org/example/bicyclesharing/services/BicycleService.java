package org.example.bicyclesharing.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.repository.BicycleRepository;
import org.example.bicyclesharing.repository.Repository;

public class BicycleService extends BaseService<Bicycle, UUID> {

  private final BicycleRepository repository;
  private final ReservationService reservationService;
  private final BikeIssueService bikeIssueService;
  private final MaintenanceRecordService maintenanceRecordService;

  public BicycleService(
      BicycleRepository repository,
      ReservationService reservationService,
      BikeIssueService bikeIssueService,
      MaintenanceRecordService maintenanceRecordService
  ) {
    this.repository = repository;
    this.reservationService = reservationService;
    this.bikeIssueService = bikeIssueService;
    this.maintenanceRecordService = maintenanceRecordService;
  }

  @Override
  protected Repository<Bicycle, UUID> getRepository() {
    return repository;
  }

  public Optional<Bicycle> getById(UUID id) {
    return repository.findById(id);
  }

  public List<Bicycle> getByFilters(String search, StateBicycle stateBicycle) {
    return repository.findByFilters(search, stateBicycle);
  }

  public List<Bicycle> getAvailable() {
    return repository.findByState(StateBicycle.AVAILABLE);
  }

  public void validateCanEdit(Bicycle bicycle) {
    validateCanModify(bicycle, true);
  }

  public void validateCanDelete(Bicycle bicycle) {
    validateCanModify(bicycle, false);
  }

  private void validateCanModify(Bicycle bicycle, boolean isEdit) {
    if (bicycle == null || bicycle.getId() == null) {
      throw new BusinessException("error.bicycle.not_found");
    }

    if (bicycle.getState() == StateBicycle.RENTED) {
      throw new BusinessException(
          isEdit ? "error.bicycle.edit.rented" : "error.bicycle.delete.rented"
      );
    }

    if (bicycle.getState() == StateBicycle.ON_MAINTENANCE) {
      throw new BusinessException(
          isEdit ? "error.bicycle.edit.on_maintenance" : "error.bicycle.delete.on_maintenance"
      );
    }

    if (bicycle.getState() == StateBicycle.NEEDS_INSPECTION) {
      throw new BusinessException(
          isEdit ? "error.bicycle.edit.needs_inspection" : "error.bicycle.delete.needs_inspection"
      );
    }

    boolean hasActiveReservation = reservationService.getAll().stream()
        .anyMatch(reservation ->
            bicycle.getId().equals(reservation.getBicycleId())
                && reservation.getStatus() != ReservationStatus.CANCELLED
                && reservation.getStatus() != ReservationStatus.ISSUED
        );

    if (hasActiveReservation) {
      throw new BusinessException(
          isEdit
              ? "error.bicycle.edit.active_reservation"
              : "error.bicycle.delete.active_reservation"
      );
    }

    boolean hasActiveIssue = bikeIssueService.getAll().stream()
        .anyMatch(issue ->
            bicycle.getId().equals(issue.getBicycleId())
                && issue.getStatus() != IssueStatus.RESOLVED
        );

    if (hasActiveIssue) {
      throw new BusinessException(
          isEdit
              ? "error.bicycle.edit.active_issue"
              : "error.bicycle.delete.active_issue"
      );
    }

    boolean hasActiveMaintenance = maintenanceRecordService.getAll().stream()
        .anyMatch(record ->
            bicycle.getId().equals(record.getBicycleId())
                && record.getAction() == null
        );

    if (hasActiveMaintenance) {
      throw new BusinessException(
          isEdit
              ? "error.bicycle.edit.active_maintenance"
              : "error.bicycle.delete.active_maintenance"
      );
    }
  }
}