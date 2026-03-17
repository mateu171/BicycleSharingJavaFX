package org.example.bicyclesharing.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.Transaction;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TransactionType;
import org.example.bicyclesharing.repository.RentalRepository;
import org.example.bicyclesharing.repository.Repository;

public class RentalService extends BaseService<Rental, UUID> {

  private final RentalRepository repository;
  private final BicycleService bicycleService;
  private final UserService userService;
  private final TransactionService transactionService;

  public RentalService(
      RentalRepository repository,
      BicycleService bicycleService, UserService userService, TransactionService transactionService
  ) {
    this.repository = repository;
    this.bicycleService = bicycleService;
    this.userService = userService;
    this.transactionService = transactionService;
  }

  @Override
  protected Repository<Rental, UUID> getRepository() {
    return repository;
  }

  public List<Rental> getByUserId(UUID id) {
    return repository.findByUserId(id);
  }

  public void finishRental(Rental rental) {
    Bicycle bicycle = bicycleService.getById(rental.getBicycleId());

    rental.setEnd(LocalDateTime.now());

    calculateCost(rental, bicycle);

    bicycle.setState(StateBicycle.AVAILABLE);
    bicycleService.update(bicycle);

    User user = userService.getById(rental.getUserId());
    user.setBalance(user.getBalance() - rental.getTotalCost());
    userService.update(user);

    Transaction transaction = new Transaction(
        user.getId(),
        rental.getTotalCost(),
        TransactionType.RENTAL_FEE,
        "transaction.rental_fee"
    );
    transactionService.add(transaction);

    repository.update(rental);

  }

  private void calculateCost(Rental rental, Bicycle bicycle) {
    Duration duration = Duration.between(rental.getStart(), rental.getEnd());
    double seconds = duration.toSeconds();

    double totalCost = (seconds / 60) * bicycle.getPricePerMinute();

    rental.setTotalCost(totalCost);
  }

}
