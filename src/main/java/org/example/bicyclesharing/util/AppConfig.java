package org.example.bicyclesharing.util;

import org.example.bicyclesharing.domain.Impl.*;
import org.example.bicyclesharing.domain.security.PasswordHasher;
import org.example.bicyclesharing.repository.*;
import org.example.bicyclesharing.repository.cache.CachedRepositoryProxy;
import org.example.bicyclesharing.repository.db.*;
import org.example.bicyclesharing.services.*;

public final class AppConfig {

  private AppConfig() {}

  private static final PasswordHasher PASSWORD_HASHER = new PasswordHasher();

  private static EmailService EMAIL_SERVICE;
  private static VerificationService VERIFICATION_SERVICE;
  private static UserService USER_SERVICE;
  private static AuthService AUTH_SERVICE;
  private static BicycleService BICYCLE_SERVICE;
  private static StationService STATION_SERVICE;
  private static RentalService RENTAL_SERVICE;
  private static BikeIssueService BIKE_ISSUE_SERVICE;
  private static MaintenanceRecordService MAINTENANCE_RECORD_SERVICE;
  private static CustomerService CUSTOMER_SERVICE;
  private static ReservationService RESERVATION_SERVICE;

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

  public static EmailService emailService() {
    if (EMAIL_SERVICE == null) {
      EMAIL_SERVICE = new EmailService();
    }
    return EMAIL_SERVICE;
  }

  public static VerificationService verificationService() {
    if (VERIFICATION_SERVICE == null) {
      VERIFICATION_SERVICE = new VerificationService(emailService());
    }
    return VERIFICATION_SERVICE;
  }

  public static UserService userService() {
    if (USER_SERVICE == null) {
      USER_SERVICE = new UserService(USER_REPOSITORY);
    }
    return USER_SERVICE;
  }

  public static AuthService authService() {
    if (AUTH_SERVICE == null) {
      AUTH_SERVICE = new AuthService(USER_REPOSITORY);
    }
    return AUTH_SERVICE;
  }

  public static BikeIssueService bikeIssueService() {
    if (BIKE_ISSUE_SERVICE == null) {
      BIKE_ISSUE_SERVICE = new BikeIssueService(BIKE_ISSUE_REPOSITORY);
    }
    return BIKE_ISSUE_SERVICE;
  }

  public static MaintenanceRecordService maintenanceRecordService() {
    if (MAINTENANCE_RECORD_SERVICE == null) {
      MAINTENANCE_RECORD_SERVICE =
          new MaintenanceRecordService(MAINTENANCE_RECORD_REPOSITORY);
    }
    return MAINTENANCE_RECORD_SERVICE;
  }

  public static ReservationService reservationService() {
    if (RESERVATION_SERVICE == null) {
      RESERVATION_SERVICE =
          new ReservationService(
              RESERVATION_REPOSITORY,
              BICYCLE_REPOSITORY,
              CUSTOMER_REPOSITORY
          );
    }
    return RESERVATION_SERVICE;
  }

  public static CustomerService customerService() {
    if (CUSTOMER_SERVICE == null) {
      CUSTOMER_SERVICE = new CustomerService(CUSTOMER_REPOSITORY);
    }
    return CUSTOMER_SERVICE;
  }

  public static BicycleService bicycleService() {
    if (BICYCLE_SERVICE == null) {
      BICYCLE_SERVICE =
          new BicycleService(
              BICYCLE_REPOSITORY,
              reservationService(),
              bikeIssueService(),
              maintenanceRecordService()
          );
    }
    return BICYCLE_SERVICE;
  }

  public static StationService stationService() {
    if (STATION_SERVICE == null) {
      STATION_SERVICE = new StationService(STATION_REPOSITORY, bicycleService());
    }
    return STATION_SERVICE;
  }

  public static RentalService rentalService() {
    if (RENTAL_SERVICE == null) {
      RENTAL_SERVICE =
          new RentalService(
              RENTAL_REPOSITORY,
              bicycleService(),
              customerService(),
              bikeIssueService()
          );
    }
    return RENTAL_SERVICE;
  }

  public static PasswordHasher passwordHasher() {
    return PASSWORD_HASHER;
  }
}