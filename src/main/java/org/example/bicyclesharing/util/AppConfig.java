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

public final class AppConfig {

  private AppConfig() {
  }

  private static final PasswordHasher PASSWORD_HASHER = new PasswordHasher();
  private static final EmailService EMAIL_SERVICE = new EmailService();


  private static final UserRepositoryDB USER_REPOSITORY = new UserRepositoryDB();
  private static final BicycleRepositoryDB BICYCLE_REPOSITORY = new BicycleRepositoryDB();
  private static final StationRepositoryDB STATION_REPOSITORY = new StationRepositoryDB();
  private static final RentalRepositoryDB RENTAL_REPOSITORY = new RentalRepositoryDB();
  private static final ReservationRepositoryDB RESERVATION_REPOSITORY = new ReservationRepositoryDB();
  private static final BikeIssueRepositoryDB BIKE_ISSUE_REPOSITORY = new BikeIssueRepositoryDB();
  private static final MaintenanceRecordRepositoryDB MAINTENANCE_RECORD_REPOSITORY =
      new MaintenanceRecordRepositoryDB();
  private static final CustomerRepositoryDB CUSTOMER_REPOSITORY = new CustomerRepositoryDB();


  private static final VerificationService VERIFICATION_SERVICE =
      new VerificationService(EMAIL_SERVICE);

  private static final BikeIssueService BIKE_ISSUE_SERVICE =
      new BikeIssueService(BIKE_ISSUE_REPOSITORY);

  private static final MaintenanceRecordService MAINTENANCE_RECORD_SERVICE =
      new MaintenanceRecordService(MAINTENANCE_RECORD_REPOSITORY);

  private static final ReservationService RESERVATION_SERVICE =
      new ReservationService(RESERVATION_REPOSITORY);

  private static final CustomerService CUSTOMER_SERVICE =
      new CustomerService(CUSTOMER_REPOSITORY);

  private static final UserService USER_SERVICE =
      new UserService(USER_REPOSITORY, PASSWORD_HASHER);

  private static final AuthService AUTH_SERVICE =
      new AuthService(USER_REPOSITORY);

  private static final BicycleService BICYCLE_SERVICE =
      new BicycleService(
          BICYCLE_REPOSITORY,
          RESERVATION_SERVICE,
          BIKE_ISSUE_SERVICE,
          MAINTENANCE_RECORD_SERVICE
      );

  private static final StationService STATION_SERVICE =
      new StationService(STATION_REPOSITORY, BICYCLE_SERVICE);

  private static final RentalService RENTAL_SERVICE =
      new RentalService(
          RENTAL_REPOSITORY,
          BICYCLE_SERVICE,
          CUSTOMER_SERVICE
      );

  public static VerificationService verificationService() {
    return VERIFICATION_SERVICE;
  }

  public static UserService userService() {
    return USER_SERVICE;
  }

  public static BicycleService bicycleService() {
    return BICYCLE_SERVICE;
  }

  public static StationService stationService() {
    return STATION_SERVICE;
  }

  public static RentalService rentalService() {
    return RENTAL_SERVICE;
  }

  public static BikeIssueService bikeIssueService() {
    return BIKE_ISSUE_SERVICE;
  }

  public static AuthService authService() {
    return AUTH_SERVICE;
  }

  public static MaintenanceRecordService maintenanceRecordService() {
    return MAINTENANCE_RECORD_SERVICE;
  }

  public static CustomerService customerService() {
    return CUSTOMER_SERVICE;
  }

  public static ReservationService reservationService() {
    return RESERVATION_SERVICE;
  }

  public static EmailService emailService() {
    return EMAIL_SERVICE;
  }

  public static PasswordHasher passwordHasher() {
    return PASSWORD_HASHER;
  }
}