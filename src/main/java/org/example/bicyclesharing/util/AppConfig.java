package org.example.bicyclesharing.util;


import org.example.bicyclesharing.domain.security.PasswordHasher;
import org.example.bicyclesharing.repository.db.UserRepositoryDB;
import org.example.bicyclesharing.services.AuthService;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.EmailService;
import org.example.bicyclesharing.services.EmployeeService;
import org.example.bicyclesharing.services.RentalService;
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
//
//  public static BicycleService bicycleService() {
//    return new BicycleService(
//        new JsonBicycleRepository("data/bicycles.json")
//    );
//  }
//
//  public static EmployeeService employeeService() {
//    return new EmployeeService(
//        new JsonEmployeeRepository("data/employees.json")
//    );
//  }
//
//  public static RentalService rentalService() {
//    return new RentalService(
//        new JsonRentalRepository("data/rentals.json"),
//        bicycleService()
//    );
//  }
//
//  public static StationService stationService() {
//    return new StationService(new JsonStationRepository("data/stations.json"));
//  }
//
//  public static AuthService authService() {
//    return new AuthService(new JsonUserRepository("data/users.json"));
//  }
}
