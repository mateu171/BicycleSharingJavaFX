package org.example.bicyclesharing.util;

import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.security.PasswordHasher;
import org.example.bicyclesharing.repository.BicycleRepository;
import org.example.bicyclesharing.repository.BikeIssueRepository;
import org.example.bicyclesharing.repository.CustomerRepository;
import org.example.bicyclesharing.repository.MaintenanceRecordRepository;
import org.example.bicyclesharing.repository.RentalRepository;
import org.example.bicyclesharing.repository.ReservationRepository;
import org.example.bicyclesharing.repository.StationRepository;
import org.example.bicyclesharing.repository.UserRepository;
import org.example.bicyclesharing.repository.cache.CachedRepositoryProxy;
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

  private static final UserRepository USER_REPOSITORY =
      CachedRepositoryProxy.create(
          new UserRepositoryDB(),
          UserRepository.class,
          User::getId
      );

  private static final BicycleRepository BICYCLE_REPOSITORY =
      CachedRepositoryProxy.create(
          new BicycleRepositoryDB(),
          BicycleRepository.class,
          Bicycle::getId
      );

  private static final StationRepository STATION_REPOSITORY =
      CachedRepositoryProxy.create(
          new StationRepositoryDB(),
          StationRepository.class,
          Station::getId
      );

  private static final RentalRepository RENTAL_REPOSITORY =
      CachedRepositoryProxy.create(
          new RentalRepositoryDB(),
          RentalRepository.class,
          Rental::getId
      );

  private static final ReservationRepository RESERVATION_REPOSITORY =
      CachedRepositoryProxy.create(
          new ReservationRepositoryDB(),
          ReservationRepository.class,
          Reservation::getId
      );

  private static final BikeIssueRepository BIKE_ISSUE_REPOSITORY =
      CachedRepositoryProxy.create(
          new BikeIssueRepositoryDB(),
          BikeIssueRepository.class,
          BikeIssue::getId
      );

  private static final MaintenanceRecordRepository MAINTENANCE_RECORD_REPOSITORY =
      CachedRepositoryProxy.create(
          new MaintenanceRecordRepositoryDB(),
          MaintenanceRecordRepository.class,
          MaintenanceRecord::getId
      );

  private static final CustomerRepository CUSTOMER_REPOSITORY =
      CachedRepositoryProxy.create(
          new CustomerRepositoryDB(),
          CustomerRepository.class,
          Customer::getId
      );

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