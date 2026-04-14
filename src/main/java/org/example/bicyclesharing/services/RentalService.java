package org.example.bicyclesharing.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.repository.RentalRepository;
import org.example.bicyclesharing.repository.Repository;

public class RentalService extends BaseService<Rental, UUID> {

  private final RentalRepository repository;
  private final BicycleService bicycleService;
  private final CustomerService customerService;

  public RentalService(
      RentalRepository repository,
      BicycleService bicycleService, CustomerService customerService) {
    this.repository = repository;
    this.bicycleService = bicycleService;
    this.customerService = customerService;
  }

  @Override
  protected Repository<Rental, UUID> getRepository() {
    return repository;
  }

  public List<Rental> getByCustomerId(UUID id) {
    return repository.findByCustomerId(id);
  }

  public Rental getById(UUID id)
  {
    return repository.findById(id).orElse(null);
  }

  public List<Rental> findActiveByFilters(String search) {
    return repository.findActiveByFilters(search);
  }

  public Rental finishRental(Rental rental) {
    if (rental == null) {
      throw new BusinessException("error.rental.not_found");
    }

    Bicycle bicycle = bicycleService.getById(rental.getBicycleId()).orElse(null);
    Customer customer = customerService.getById(rental.getCustomerId()).orElse(null);

    if (bicycle == null) {
      throw new BusinessException("error.bicycle.not_found");
    }

    if (customer == null) {
      throw new BusinessException("error.customer.not_found");
    }

    if (rental.getEnd() != null) {
      throw new BusinessException("error.rental.already_finished");
    }

    LocalDateTime endTime = LocalDateTime.now();
    rental.setEnd(endTime);

    long minutes = Duration.between(rental.getStart(), endTime).toMinutes();
    if (minutes <= 0) {
      minutes = 1;
    }

    double totalCost = minutes * bicycle.getPricePerMinute();
    rental.setTotalCost(totalCost);

    customer.setActiveRent(null);
    customerService.update(customer);
    update(rental);
    return rental;
  }
}
