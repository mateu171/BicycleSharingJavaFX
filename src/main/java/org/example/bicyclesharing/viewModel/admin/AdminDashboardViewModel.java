package org.example.bicyclesharing.viewModel.admin;

import java.time.format.DateTimeFormatter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.dto.LatestIssueInfo;
import org.example.bicyclesharing.dto.LatestRentalInfo;
import org.example.bicyclesharing.dto.LatestReservationInfo;
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

  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("admin.dashboard.title");

  private final StringProperty subtitleText =
      LocalizationManager.getStringProperty("admin.dashboard.subtitle");

  private final StringProperty totalUsersTitle =
      LocalizationManager.getStringProperty("admin.dashboard.total_users");

  private final StringProperty totalBicyclesTitle =
      LocalizationManager.getStringProperty("admin.dashboard.total_bicycles");

  private final StringProperty activeRentalsTitle =
      LocalizationManager.getStringProperty("admin.dashboard.active_rentals");

  private final StringProperty activeReservationsTitle =
      LocalizationManager.getStringProperty("admin.dashboard.active_reservations");

  private final StringProperty totalUsersValue =
      new SimpleStringProperty("...");

  private final StringProperty totalBicyclesValue =
      new SimpleStringProperty("...");

  private final StringProperty activeRentalsValue =
      new SimpleStringProperty("...");

  private final StringProperty activeReservationsValue =
      new SimpleStringProperty("...");

  private final StringProperty attentionTitle =
      LocalizationManager.getStringProperty("admin.dashboard.attention_title");

  private final StringProperty latestActivityTitle =
      LocalizationManager.getStringProperty("admin.dashboard.latest_activity_title");

  private final StringProperty quickActionsTitle =
      LocalizationManager.getStringProperty("admin.dashboard.quick_actions_title");

  private final StringProperty needsInspectionText =
      new SimpleStringProperty("...");

  private final StringProperty onMaintenanceText =
      new SimpleStringProperty("...");

  private final StringProperty unavailableText =
      new SimpleStringProperty("...");

  private final StringProperty newIssuesText =
      new SimpleStringProperty("...");

  private final StringProperty totalStationsText =
      new SimpleStringProperty("...");

  private final StringProperty latestRentalText =
      new SimpleStringProperty("...");

  private final StringProperty latestReservationText =
      new SimpleStringProperty("...");

  private final StringProperty latestIssueText =
      new SimpleStringProperty("...");

  private final StringProperty openUsersButtonText =
      LocalizationManager.getStringProperty("admin.dashboard.open_users");

  private final StringProperty openBicyclesButtonText =
      LocalizationManager.getStringProperty("admin.dashboard.open_bicycles");

  private final StringProperty openStationsButtonText =
      LocalizationManager.getStringProperty("admin.dashboard.open_stations");

  private final BooleanProperty loading =
      new SimpleBooleanProperty(false);

  private final StringProperty errorText =
      new SimpleStringProperty("");

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

  public void initialize() {
    loadAsync();
  }

  public void loadAsync() {

    loading.set(true);
    errorText.set("");

    Task<AdminDashboardData> task = new Task<>() {

      @Override
      protected AdminDashboardData call() {

        reservationService.updateStatuses();

        long totalUsers =
            userService.count();

        long totalBicycles =
            bicycleService.count();

        long totalStations =
            stationService.count();

        long activeRentals =
            rentalService.countActiveRentals();

        long activeReservations =
            reservationService.countByStatuses(
                ReservationStatus.NEW,
                ReservationStatus.ISSUED
            );

        long needsInspection =
            bicycleService.countByState(
                StateBicycle.NEEDS_INSPECTION
            );

        long onMaintenance =
            bicycleService.countByState(
                StateBicycle.ON_MAINTENANCE
            );

        long unavailable =
            bicycleService.countByState(
                StateBicycle.UNAVAILABLE
            );

        long newIssues =
            bikeIssueService.countByIssueStatus(
                IssueStatus.NEW
            );

        LatestRentalInfo latestRental =
            rentalService.getLatestRentalInfo();

        LatestReservationInfo latestReservation =
            reservationService.getLatestReservationInfo();

        LatestIssueInfo latestIssue =
            bikeIssueService.getLatestIssueInfo();

        String latestRentalValue =
            buildLatestRentalText(latestRental);

        String latestReservationValue =
            buildLatestReservationText(latestReservation);

        String latestIssueValue =
            buildLatestIssueText(latestIssue);

        return new AdminDashboardData(
            String.valueOf(totalUsers),
            String.valueOf(totalBicycles),
            String.valueOf(activeRentals),
            String.valueOf(activeReservations),

            LocalizationManager.getStringByKey(
                "admin.dashboard.needs_inspection"
            ) + ": " + needsInspection,

            LocalizationManager.getStringByKey(
                "admin.dashboard.on_maintenance"
            ) + ": " + onMaintenance,

            LocalizationManager.getStringByKey(
                "admin.dashboard.unavailable_bicycles"
            ) + ": " + unavailable,

            LocalizationManager.getStringByKey(
                "admin.dashboard.new_issues"
            ) + ": " + newIssues,

            LocalizationManager.getStringByKey(
                "admin.dashboard.total_stations"
            ) + ": " + totalStations,

            latestRentalValue,
            latestReservationValue,
            latestIssueValue
        );
      }
    };

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

    task.setOnFailed(event -> {

      Throwable exception = task.getException();

      if (exception != null) {
        exception.printStackTrace();
      }

      errorText.set(
          LocalizationManager.getStringByKey(
              "error.dashboard.load"
          )
      );

      loading.set(false);
    });

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }

  private String buildLatestRentalText(
      LatestRentalInfo rental
  ) {

    if (rental == null) {
      return LocalizationManager.getStringByKey(
          "admin.dashboard.no_data"
      );
    }

    return LocalizationManager.getStringByKey(
        "admin.dashboard.latest_rental"
    )
        + ": "
        + rental.customerName()
        + " — "
        + rental.bicycleModel()
        + " — "
        + rental.start().format(formatter);
  }

  private String buildLatestReservationText(
      LatestReservationInfo reservation
  ) {

    if (reservation == null) {
      return LocalizationManager.getStringByKey(
          "admin.dashboard.no_data"
      );
    }

    return LocalizationManager.getStringByKey(
        "admin.dashboard.latest_reservation"
    )
        + ": "
        + reservation.customerName()
        + " — "
        + reservation.bicycleModel()
        + " — "
        + reservation.start().format(formatter);
  }

  private String buildLatestIssueText(
      LatestIssueInfo issue
  ) {

    if (issue == null) {
      return LocalizationManager.getStringByKey(
          "admin.dashboard.no_data"
      );
    }

    return LocalizationManager.getStringByKey(
        "admin.dashboard.latest_issue"
    )
        + ": "
        + issue.bicycleModel()
        + " — "
        + safe(issue.problemType())
        + " — "
        + issue.createdAt().format(formatter);
  }

  private String safe(String value) {

    return value == null || value.isBlank()
        ? LocalizationManager.getStringByKey(
        "admin.dashboard.no_data"
    )
        : value;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty subtitleTextProperty() {
    return subtitleText;
  }

  public StringProperty totalUsersTitleProperty() {
    return totalUsersTitle;
  }

  public StringProperty totalBicyclesTitleProperty() {
    return totalBicyclesTitle;
  }

  public StringProperty activeRentalsTitleProperty() {
    return activeRentalsTitle;
  }

  public StringProperty activeReservationsTitleProperty() {
    return activeReservationsTitle;
  }

  public StringProperty totalUsersValueProperty() {
    return totalUsersValue;
  }

  public StringProperty totalBicyclesValueProperty() {
    return totalBicyclesValue;
  }

  public StringProperty activeRentalsValueProperty() {
    return activeRentalsValue;
  }

  public StringProperty activeReservationsValueProperty() {
    return activeReservationsValue;
  }

  public StringProperty attentionTitleProperty() {
    return attentionTitle;
  }

  public StringProperty latestActivityTitleProperty() {
    return latestActivityTitle;
  }

  public StringProperty quickActionsTitleProperty() {
    return quickActionsTitle;
  }

  public StringProperty needsInspectionTextProperty() {
    return needsInspectionText;
  }

  public StringProperty onMaintenanceTextProperty() {
    return onMaintenanceText;
  }

  public StringProperty unavailableTextProperty() {
    return unavailableText;
  }

  public StringProperty newIssuesTextProperty() {
    return newIssuesText;
  }

  public StringProperty totalStationsTextProperty() {
    return totalStationsText;
  }

  public StringProperty latestRentalTextProperty() {
    return latestRentalText;
  }

  public StringProperty latestReservationTextProperty() {
    return latestReservationText;
  }

  public StringProperty latestIssueTextProperty() {
    return latestIssueText;
  }

  public StringProperty openUsersButtonTextProperty() {
    return openUsersButtonText;
  }

  public StringProperty openBicyclesButtonTextProperty() {
    return openBicyclesButtonText;
  }

  public StringProperty openStationsButtonTextProperty() {
    return openStationsButtonText;
  }

  public BooleanProperty loadingProperty() {
    return loading;
  }

  public StringProperty errorTextProperty() {
    return errorText;
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
  ) {}
}