package org.example.bicyclesharing.viewModel.admin;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
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

  public final StringProperty totalUsersValue = new SimpleStringProperty("...");
  public final StringProperty totalBicyclesValue = new SimpleStringProperty("...");
  public final StringProperty activeRentalsValue = new SimpleStringProperty("...");
  public final StringProperty activeReservationsValue = new SimpleStringProperty("...");

  public final StringProperty attentionTitle =
      LocalizationManager.getStringProperty("admin.dashboard.attention_title");
  public final StringProperty latestActivityTitle =
      LocalizationManager.getStringProperty("admin.dashboard.latest_activity_title");
  public final StringProperty quickActionsTitle =
      LocalizationManager.getStringProperty("admin.dashboard.quick_actions_title");

  public final StringProperty needsInspectionText = new SimpleStringProperty("...");
  public final StringProperty onMaintenanceText = new SimpleStringProperty("...");
  public final StringProperty unavailableText = new SimpleStringProperty("...");
  public final StringProperty newIssuesText = new SimpleStringProperty("...");
  public final StringProperty totalStationsText = new SimpleStringProperty("...");

  public final StringProperty latestRentalText = new SimpleStringProperty("...");
  public final StringProperty latestReservationText = new SimpleStringProperty("...");
  public final StringProperty latestIssueText = new SimpleStringProperty("...");

  public final StringProperty openUsersButtonText =
      LocalizationManager.getStringProperty("admin.dashboard.open_users");
  public final StringProperty openBicyclesButtonText =
      LocalizationManager.getStringProperty("admin.dashboard.open_bicycles");
  public final StringProperty openStationsButtonText =
      LocalizationManager.getStringProperty("admin.dashboard.open_stations");

  public final BooleanProperty loading = new SimpleBooleanProperty(false);

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
  }

  public void loadAsync() {
    Task<AdminDashboardData> task = new Task<>() {
      @Override
      protected AdminDashboardData call() {
        reservationService.updateStatuses();

        List<User> users = userService.getAll();
        List<Bicycle> bicycles = bicycleService.getAll();
        List<Rental> rentals = rentalService.getAll();
        List<Reservation> reservations = reservationService.getAll();
        List<BikeIssue> issues = bikeIssueService.getAll();
        List<Customer> customers = customerService.getAll();

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

        String latestRental = buildLatestRentalText(rentals, customers, bicycles);
        String latestReservation = buildLatestReservationText(reservations, customers, bicycles);
        String latestIssue = buildLatestIssueText(issues, bicycles);

        return new AdminDashboardData(
            String.valueOf(users.size()),
            String.valueOf(bicycles.size()),
            String.valueOf(activeRentals),
            String.valueOf(activeReservations),
            LocalizationManager.getStringByKey("admin.dashboard.needs_inspection") + ": "
                + needsInspection,
            LocalizationManager.getStringByKey("admin.dashboard.on_maintenance") + ": "
                + onMaintenance,
            LocalizationManager.getStringByKey("admin.dashboard.unavailable_bicycles") + ": "
                + unavailable,
            LocalizationManager.getStringByKey("admin.dashboard.new_issues") + ": " + newIssues,
            LocalizationManager.getStringByKey("admin.dashboard.total_stations") + ": "
                + stationService.getAll().size(),
            latestRental,
            latestReservation,
            latestIssue
        );
      }
    };
loading.set(true);

task.setOnSucceeded(event -> {
  AdminDashboardData data = task.getValue();

  totalUsersValue.set(data.totalUsers());
  totalBicyclesValue.set(data.totalBicycles());
  activeRentalsValue.set(data.activeRentals());
  activeReservationsValue.set(data.activeReservations());

  needsInspectionText.set(data.needsInspectionText());
  onMaintenanceText.set(data.onMaintenanceText());
  unavailableText.set(data.unavailableText());
  newIssuesText.set(data.newIssuesText());
  totalStationsText.set(data.totalStationsText());

  latestRentalText.set(data.latestRentalText());
  latestReservationText.set(data.latestReservationText());
  latestIssueText.set(data.latestIssueText());

  loading.set(false);
});

   task.setOnFailed(event ->
   {
     task.getException().printStackTrace();
     loading.set(false);
   });

   Thread thread = new Thread(task);
   thread.setDaemon(true);
   thread.start();
  }

  private String buildLatestRentalText(
      List<Rental> rentals,
      List<Customer> customers,
      List<Bicycle> bicycles
  ) {
    return rentals.stream()
        .sorted(Comparator.comparing(Rental::getStart).reversed())
        .findFirst()
        .map(rental ->
            LocalizationManager.getStringByKey("admin.dashboard.latest_rental")
                + ": "
                + getCustomerName(rental.getCustomerId(), customers)
                + " — "
                + getBicycleModel(rental.getBicycleId(), bicycles)
                + " — "
                + rental.getStart().format(formatter)
        )
        .orElse(LocalizationManager.getStringByKey("admin.dashboard.no_data"));
  }

  private String buildLatestReservationText(
      List<Reservation> reservations,
      List<Customer> customers,
      List<Bicycle> bicycles
  ) {
    return reservations.stream()
        .sorted(Comparator.comparing(Reservation::getStartTime).reversed())
        .findFirst()
        .map(reservation ->
            LocalizationManager.getStringByKey("admin.dashboard.latest_reservation")
                + ": "
                + getCustomerName(reservation.getCustomerId(), customers)
                + " — "
                + getBicycleModel(reservation.getBicycleId(), bicycles)
                + " — "
                + reservation.getStartTime().format(formatter)
        )
        .orElse(LocalizationManager.getStringByKey("admin.dashboard.no_data"));
  }

  private String buildLatestIssueText(List<BikeIssue> issues, List<Bicycle> bicycles) {
    return issues.stream()
        .sorted(Comparator.comparing(BikeIssue::getCreatedAt).reversed())
        .findFirst()
        .map(issue ->
            LocalizationManager.getStringByKey("admin.dashboard.latest_issue")
                + ": "
                + getBicycleModel(issue.getBicycleId(), bicycles)
                + " — "
                + safe(issue.getProblemType())
                + " — "
                + issue.getCreatedAt().format(formatter)
        )
        .orElse(LocalizationManager.getStringByKey("admin.dashboard.no_data"));
  }

  private String getCustomerName(UUID customerId, List<Customer> customers) {
    if (customerId == null) {
      return LocalizationManager.getStringByKey("admin.dashboard.unknown_customer");
    }

    return customers.stream()
        .filter(customer -> customerId.equals(customer.getId()))
        .map(Customer::getFullName)
        .findFirst()
        .orElse(LocalizationManager.getStringByKey("admin.dashboard.unknown_customer"));
  }

  private String getBicycleModel(UUID bicycleId, List<Bicycle> bicycles) {
    if (bicycleId == null) {
      return LocalizationManager.getStringByKey("admin.dashboard.unknown_bicycle");
    }

    return bicycles.stream()
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

  private record AdminDashboardData(
      String totalUsers,
      String totalBicycles,
      String activeRentals,
      String activeReservations,
      String needsInspectionText,
      String onMaintenanceText,
      String unavailableText,
      String newIssuesText,
      String totalStationsText,
      String latestRentalText,
      String latestReservationText,
      String latestIssueText
  ){}
}