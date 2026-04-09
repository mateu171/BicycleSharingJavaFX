package org.example.bicyclesharing.util;


import org.example.bicyclesharing.domain.security.PasswordHasher;
import org.example.bicyclesharing.repository.db.BicycleRepositoryDB;
import org.example.bicyclesharing.repository.db.BikeIssueRepositoryDB;
import org.example.bicyclesharing.repository.db.CustomerRepositoryDB;
import org.example.bicyclesharing.repository.db.MaintenanceRecordRepositoryDB;
import org.example.bicyclesharing.repository.db.RentalRepositoryDB;
import org.example.bicyclesharing.repository.db.ReservationRepositoryDB;
import org.example.bicyclesharing.repository.db.StationRepositoryDB;
import org.example.bicyclesharing.repository.db.UserRepositoryDB;
import org.example.bicyclesharing.services.AuthService;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.BikeIssueService;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.services.EmailService;
import org.example.bicyclesharing.services.MaintenanceRecordService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.services.ReservationService;
import org.example.bicyclesharing.services.StationService;
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

  public static StationService stationService()
  {
    return new StationService(new StationRepositoryDB());
  }


  public static RentalService rentalService() {
    return new RentalService(
        new RentalRepositoryDB(),
        bicycleService(),
        customerService()
    );
  }

  public static BikeIssueService bikeIssueService() {
    return new BikeIssueService(new BikeIssueRepositoryDB());
  }

  public static AuthService authService() {
    return new AuthService(new UserRepositoryDB());
  }

  public static MaintenanceRecordService maintenanceRecordService() {
    return new MaintenanceRecordService(
        new MaintenanceRecordRepositoryDB()
    );
  }

  public static CustomerService customerService()
  {
    return new CustomerService(new CustomerRepositoryDB());
  }

  public static ReservationService reservationService()
  {
    return new ReservationService(new ReservationRepositoryDB());
  }
}
