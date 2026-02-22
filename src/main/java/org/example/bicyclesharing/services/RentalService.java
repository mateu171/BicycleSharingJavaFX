package org.example.bicyclesharing.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.enums.RentalStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.repository.RentalRepository;
import org.example.bicyclesharing.repository.Repository;

public class RentalService extends BaseService<Rental, UUID> {

  private final RentalRepository repository;
  private final BicycleService bicycleService;

  public RentalService(
      RentalRepository repository,
      BicycleService bicycleService
  ) {
    this.repository = repository;
    this.bicycleService = bicycleService;
  }

  @Override
  protected Repository<Rental, UUID> getRepository() {
    return repository;
  }

  public List<Rental> getByUserId(UUID id) {
    return repository.findByUserId(id);
  }

  public List<Rental> getByStationId(UUID id) {
    return repository.findByStationId(id);
  }

  public List<Rental> getByActiveRentals() {
    return repository.findByRentalStatus(RentalStatus.ACTIVE);
  }

  public List<Rental> getByActiveAndUserId(UUID id) {
    return repository.findByUserId(id).stream()
        .filter(s -> s.getRentalStatus().equals(RentalStatus.ACTIVE)).toList();
  }

  public Rental finishRental(Rental rental) {
    Bicycle bicycle = bicycleService.getById(rental.getBicycleId());

    rental.setRentalStatus(RentalStatus.INACTIVE);
    rental.setEnd(LocalDateTime.now());

    calculateCost(rental, bicycle);

    bicycle.setState(StateBicycle.AVAILABLE);

    bicycleService.update(bicycle);
    repository.update(rental);

    return rental;
  }

  private void calculateCost(Rental rental, Bicycle bicycle) {
    Duration duration = Duration.between(
        rental.getStart(),
        rental.getEnd()
    );

    double hours = Math.ceil(duration.toMinutes() / 60.0);
    rental.setTotalCost(hours * bicycle.getPricePerHour());
  }

}
