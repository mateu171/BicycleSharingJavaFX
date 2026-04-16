package org.example.bicyclesharing.viewModel.admin;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.BikeIssueService;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.services.ReservationService;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class AdminDashboardViewModel extends BaseViewModel {

  private final UserService userService;
  private final BicycleService bicycleService;
  private final StationService stationService;
  private final RentalService rentalService;
  private final ReservationService reservationService;
  private final BikeIssueService bikeIssueService;
  private final CustomerService customerService;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("admin.dashboard.title");
  public final StringProperty subtitleText =
      LocalizationManager.getStringProperty("admin.dashboard.subtitle");

  public final StringProperty totalUsersTitle =
      LocalizationManager.getStringProperty("admin.dashboard.total_users");
  public final StringProperty totalBicyclesTitle =
      LocalizationManager.getStringProperty("admin.dashboard.total_bicycles");
  public final StringProperty activeRentalsTitle =
      LocalizationManager.getStringProperty("admin.dashboard.active_rentals");
  public final StringProperty activeReservationsTitle =
      LocalizationManager.getStringProperty("admin.dashboard.active_reservations");

  public final StringProperty totalUsersValue = new SimpleStringProperty("0");
  public final StringProperty totalBicyclesValue = new SimpleStringProperty("0");
  public final StringProperty activeRentalsValue = new SimpleStringProperty("0");
  public final StringProperty activeReservationsValue = new SimpleStringProperty("0");

  public final StringProperty attentionTitle =
      LocalizationManager.getStringProperty("admin.dashboard.attention_title");
  public final StringProperty latestActivityTitle =
      LocalizationManager.getStringProperty("admin.dashboard.latest_activity_title");
  public final StringProperty quickActionsTitle =
      LocalizationManager.getStringProperty("admin.dashboard.quick_actions_title");

  public final StringProperty needsInspectionText = new SimpleStringProperty();
  public final StringProperty onMaintenanceText = new SimpleStringProperty();
  public final StringProperty unavailableText = new SimpleStringProperty();
  public final StringProperty newIssuesText = new SimpleStringProperty();
  public final StringProperty totalStationsText = new SimpleStringProperty();

  public final StringProperty latestRentalText = new SimpleStringProperty();
  public final StringProperty latestReservationText = new SimpleStringProperty();
  public final StringProperty latestIssueText = new SimpleStringProperty();

  public final StringProperty openUsersButtonText =
      LocalizationManager.getStringProperty("admin.dashboard.open_users");
  public final StringProperty openBicyclesButtonText =
      LocalizationManager.getStringProperty("admin.dashboard.open_bicycles");
  public final StringProperty openStationsButtonText =
      LocalizationManager.getStringProperty("admin.dashboard.open_stations");

  public AdminDashboardViewModel(
      User currentUser,
      UserService userService,
      BicycleService bicycleService,
      StationService stationService,
      RentalService rentalService,
      ReservationService reservationService,
      BikeIssueService bikeIssueService,
      CustomerService customerService
  ) {
    super(currentUser);
    this.userService = userService;
    this.bicycleService = bicycleService;
    this.stationService = stationService;
    this.rentalService = rentalService;
    this.reservationService = reservationService;
    this.bikeIssueService = bikeIssueService;
    this.customerService = customerService;

    load();
  }

  public void load() {
    reservationService.updateStatuses();

    List<User> users = userService.getAll();
    List<Bicycle> bicycles = bicycleService.getAll();
    List<Rental> rentals = rentalService.getAll();
    List<Reservation> reservations = reservationService.getAll();
    List<BikeIssue> issues = bikeIssueService.getAll();

    long activeRentals = rentals.stream()
        .filter(rental -> rental.getEnd() == null)
        .count();

    long activeReservations = reservations.stream()
        .filter(reservation ->
            reservation.getStatus() == ReservationStatus.NEW
                || reservation.getStatus() == ReservationStatus.ISSUED)
        .count();

    long needsInspection = bicycles.stream()
        .filter(bicycle -> bicycle.getState() == StateBicycle.NEEDS_INSPECTION)
        .count();

    long onMaintenance = bicycles.stream()
        .filter(bicycle -> bicycle.getState() == StateBicycle.ON_MAINTENANCE)
        .count();

    long unavailable = bicycles.stream()
        .filter(bicycle -> bicycle.getState() == StateBicycle.UNAVAILABLE)
        .count();

    long newIssues = issues.stream()
        .filter(issue -> issue.getStatus() == IssueStatus.NEW)
        .count();

    totalUsersValue.set(String.valueOf(users.size()));
    totalBicyclesValue.set(String.valueOf(bicycles.size()));
    activeRentalsValue.set(String.valueOf(activeRentals));
    activeReservationsValue.set(String.valueOf(activeReservations));

    needsInspectionText.set(
        LocalizationManager.getStringByKey("admin.dashboard.needs_inspection") + ": " + needsInspection
    );
    onMaintenanceText.set(
        LocalizationManager.getStringByKey("admin.dashboard.on_maintenance") + ": " + onMaintenance
    );
    unavailableText.set(
        LocalizationManager.getStringByKey("admin.dashboard.unavailable_bicycles") + ": " + unavailable
    );
    newIssuesText.set(
        LocalizationManager.getStringByKey("admin.dashboard.new_issues") + ": " + newIssues
    );
    totalStationsText.set(
        LocalizationManager.getStringByKey("admin.dashboard.total_stations") + ": " + stationService.getAll().size()
    );

    latestRentalText.set(buildLatestRentalText(rentals));
    latestReservationText.set(buildLatestReservationText(reservations));
    latestIssueText.set(buildLatestIssueText(issues));
  }

  private String buildLatestRentalText(List<Rental> rentals) {
    return rentals.stream()
        .sorted(Comparator.comparing(Rental::getStart).reversed())
        .findFirst()
        .map(rental -> {
          String customerName = getCustomerName(rental.getCustomerId());
          String bicycleModel = getBicycleModel(rental.getBicycleId());
          return LocalizationManager.getStringByKey("admin.dashboard.latest_rental")
              + ": "
              + customerName
              + " — "
              + bicycleModel
              + " — "
              + rental.getStart().format(formatter);
        })
        .orElse(LocalizationManager.getStringByKey("admin.dashboard.no_data"));
  }

  private String buildLatestReservationText(List<Reservation> reservations) {
    return reservations.stream()
        .sorted(Comparator.comparing(Reservation::getStartTime).reversed())
        .findFirst()
        .map(reservation -> {
          String customerName = getCustomerName(reservation.getCustomerId());
          String bicycleModel = getBicycleModel(reservation.getBicycleId());
          return LocalizationManager.getStringByKey("admin.dashboard.latest_reservation")
              + ": "
              + customerName
              + " — "
              + bicycleModel
              + " — "
              + reservation.getStartTime().format(formatter);
        })
        .orElse(LocalizationManager.getStringByKey("admin.dashboard.no_data"));
  }

  private String buildLatestIssueText(List<BikeIssue> issues) {
    return issues.stream()
        .sorted(Comparator.comparing(BikeIssue::getCreatedAt).reversed())
        .findFirst()
        .map(issue -> {
          String bicycleModel = getBicycleModel(issue.getBicycleId());
          return LocalizationManager.getStringByKey("admin.dashboard.latest_issue")
              + ": "
              + bicycleModel
              + " — "
              + safe(issue.getProblemType())
              + " — "
              + issue.getCreatedAt().format(formatter);
        })
        .orElse(LocalizationManager.getStringByKey("admin.dashboard.no_data"));
  }

  private String getCustomerName(UUID customerId) {
    if (customerId == null) {
      return LocalizationManager.getStringByKey("admin.dashboard.unknown_customer");
    }

    return customerService.getAll().stream()
        .filter(customer -> customerId.equals(customer.getId()))
        .map(Customer::getFullName)
        .findFirst()
        .orElse(LocalizationManager.getStringByKey("admin.dashboard.unknown_customer"));
  }

  private String getBicycleModel(UUID bicycleId) {
    if (bicycleId == null) {
      return LocalizationManager.getStringByKey("admin.dashboard.unknown_bicycle");
    }

    return bicycleService.getAll().stream()
        .filter(bicycle -> bicycleId.equals(bicycle.getId()))
        .map(Bicycle::getModel)
        .findFirst()
        .orElse(LocalizationManager.getStringByKey("admin.dashboard.unknown_bicycle"));
  }

  private String safe(String value) {
    return value == null || value.isBlank()
        ? LocalizationManager.getStringByKey("admin.dashboard.no_data")
        : value;
  }
}