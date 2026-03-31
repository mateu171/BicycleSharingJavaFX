package org.example.bicyclesharing.util;


import org.example.bicyclesharing.domain.security.PasswordHasher;
import org.example.bicyclesharing.repository.db.BicycleRepositoryDB;
import org.example.bicyclesharing.repository.db.BikeIssueRepositoryDB;
import org.example.bicyclesharing.repository.db.EmployeeRepositoryDB;
import org.example.bicyclesharing.repository.db.RentalRepositoryDB;
import org.example.bicyclesharing.repository.db.StationRepositoryDB;
import org.example.bicyclesharing.repository.db.TransactionRepositoryDB;
import org.example.bicyclesharing.repository.db.UserRepositoryDB;
import org.example.bicyclesharing.services.AuthService;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.BikeIssueService;
import org.example.bicyclesharing.services.EmailService;
import org.example.bicyclesharing.services.EmployeeService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.services.TransactionService;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.services.VerificationService;

public class AppConfig {

  public static VerificationService verificationService() {
    return new VerificationService(
        new EmailService(
        )
    );
  }

  public static UserService userService() {
    return new UserService(
        new UserRepositoryDB(),
        new PasswordHasher()
    );
  }

  public static BicycleService bicycleService() {
    return new BicycleService(
        new BicycleRepositoryDB() {
        }
    );
  }

  public static EmployeeService employeeService() {
    return new EmployeeService(
        new EmployeeRepositoryDB()
    );
  }

  public static StationService stationService()
  {
    return new StationService(new StationRepositoryDB());
  }


  public static RentalService rentalService() {
    return new RentalService(
        new RentalRepositoryDB(),
        bicycleService(),
        userService(),
        transactionService()
    );
  }

  public static TransactionService transactionService()
  {
    return new TransactionService(new TransactionRepositoryDB());
  }

  public static BikeIssueService bikeIssueService() {
    return new BikeIssueService(new BikeIssueRepositoryDB());
  }

  public static AuthService authService() {
    return new AuthService(new UserRepositoryDB());
  }
}
