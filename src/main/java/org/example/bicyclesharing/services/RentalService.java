package org.example.bicyclesharing.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.dto.LatestRentalInfo;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.repository.RentalRepository;
import org.example.bicyclesharing.repository.Repository;

public class RentalService extends BaseService<Rental, UUID> {

  private final RentalRepository repository;
  private final BicycleService bicycleService;
  private final CustomerService customerService;
  private final BikeIssueService bikeIssueService;

  public RentalService(
      RentalRepository repository,
      BicycleService bicycleService,
      CustomerService customerService,
      BikeIssueService bikeIssueService
  ) {
    this.repository = repository;
    this.bicycleService = bicycleService;
    this.customerService = customerService;
    this.bikeIssueService = bikeIssueService;
  }

  @Override
  protected Repository<Rental, UUID> getRepository() {
    return repository;
  }

  public Rental getById(UUID id) {
    return repository.findById(id).orElse(null);
  }

  public List<Rental> getByCustomerId(UUID id) {
    return repository.findByCustomerId(id);
  }

  public List<Rental> findActiveByFilters(String search) {
    return repository.findByFilters(search);
  }

  public long countActiveRentals() {
    return repository.countActiveRentals();
  }

  public LatestRentalInfo getLatestRentalInfo() {
    return repository.getLatestRentalInfo();
  }

  public double finishRental(
      Rental rental,
      boolean hasProblem,
      String problemType,
      String comment,
      boolean technicalProblem
  ) {
    if (rental == null) {
      throw new BusinessException("error.rental.not_found");
    }

    if (rental.getEnd() != null) {
      throw new BusinessException("error.rental.already_finished");
    }

    return executeInTransactionWithResult(() -> {
      Bicycle bicycle = bicycleService.getById(rental.getBicycleId())
          .orElseThrow(() -> new BusinessException("error.bicycle.not_found"));

      Customer customer = customerService.getById(rental.getCustomerId())
          .orElseThrow(() -> new BusinessException("error.customer.not_found"));

      BikeIssue issue = null;

      if (hasProblem) {
        issue = new BikeIssue(
            rental.getId(),
            bicycle.getId(),
            problemType,
            comment,
            technicalProblem
        );
      }

      rental.setEnd(LocalDateTime.now());
      calculateCost(rental, bicycle);
      update(rental);

      customer.setActiveRent(null);
      customerService.update(customer);

      if (hasProblem) {
        bicycle.setState(StateBicycle.NEEDS_INSPECTION);
        bicycleService.update(bicycle);
        bikeIssueService.add(issue);
      } else {
        bicycle.setState(StateBicycle.AVAILABLE);
        bicycleService.update(bicycle);
      }

      return rental.getTotalCost();
    });
  }

  private void calculateCost(Rental rental, Bicycle bicycle) {
    Duration duration = Duration.between(rental.getStart(), rental.getEnd());
    double seconds = duration.toSeconds();

    double totalCost =
        Math.round((seconds / 60.0) * bicycle.getPricePerMinute() * 100.0) / 100.0;

    rental.setTotalCost(totalCost);
  }
}