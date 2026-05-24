package org.example.bicyclesharing.viewModel.manager;

import java.time.format.DateTimeFormatter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.dto.LatestCustomerInfo;
import org.example.bicyclesharing.dto.LatestRentalInfo;
import org.example.bicyclesharing.dto.LatestReservationInfo;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.services.ReservationService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class ManagerDashboardViewModel extends BaseViewModel {

  private final RentalService rentalService;
  private final ReservationService reservationService;
  private final CustomerService customerService;
  private final BicycleService bicycleService;

  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("manager.dashboard.title");

  private final StringProperty subtitleText =
      LocalizationManager.getStringProperty("manager.dashboard.subtitle");

  private final StringProperty activeRentalsTitle =
      LocalizationManager.getStringProperty("manager.dashboard.active_rentals");

  private final StringProperty activeReservationsTitle =
      LocalizationManager.getStringProperty("manager.dashboard.active_reservations");

  private final StringProperty totalCustomersTitle =
      LocalizationManager.getStringProperty("manager.dashboard.total_customers");

  private final StringProperty availableBicyclesTitle =
      LocalizationManager.getStringProperty("manager.dashboard.available_bicycles");

  private final StringProperty activeRentalsValue =
      new SimpleStringProperty("...");

  private final StringProperty activeReservationsValue =
      new SimpleStringProperty("...");

  private final StringProperty totalCustomersValue =
      new SimpleStringProperty("...");

  private final StringProperty availableBicyclesValue =
      new SimpleStringProperty("...");

  private final StringProperty attentionTitle =
      LocalizationManager.getStringProperty("manager.dashboard.attention_title");

  private final StringProperty latestActivityTitle =
      LocalizationManager.getStringProperty("manager.dashboard.latest_activity_title");

  private final StringProperty quickActionsTitle =
      LocalizationManager.getStringProperty("manager.dashboard.quick_actions_title");

  private final StringProperty issuedReservationsText =
      new SimpleStringProperty("...");

  private final StringProperty newReservationsText =
      new SimpleStringProperty("...");

  private final StringProperty rentedBicyclesText =
      new SimpleStringProperty("...");

  private final StringProperty unavailableBicyclesText =
      new SimpleStringProperty("...");

  private final StringProperty totalBicyclesText =
      new SimpleStringProperty("...");

  private final StringProperty latestRentalText =
      new SimpleStringProperty("...");

  private final StringProperty latestReservationText =
      new SimpleStringProperty("...");

  private final StringProperty latestCustomerText =
      new SimpleStringProperty("...");

  private final StringProperty openCustomersButtonText =
      LocalizationManager.getStringProperty("manager.dashboard.open_customers");

  private final StringProperty openRentalsButtonText =
      LocalizationManager.getStringProperty("manager.dashboard.open_rentals");

  private final StringProperty openReservationsButtonText =
      LocalizationManager.getStringProperty("manager.dashboard.open_reservations");

  private final BooleanProperty loading =
      new SimpleBooleanProperty(false);

  private final StringProperty errorText =
      new SimpleStringProperty("");

  public ManagerDashboardViewModel(
      User currentUser,
      RentalService rentalService,
      ReservationService reservationService,
      CustomerService customerService,
      BicycleService bicycleService
  ) {
    super(currentUser);
    this.rentalService = rentalService;
    this.reservationService = reservationService;
    this.customerService = customerService;
    this.bicycleService = bicycleService;
  }

  public void initialize() {
    loadAsync();
  }

  public void loadAsync() {
    loading.set(true);
    errorText.set("");

    Task<ManagerDashboardData> task = new Task<>() {
      @Override
      protected ManagerDashboardData call() {
        reservationService.updateStatuses();

        long totalBicycles = bicycleService.count();
        long totalCustomers = customerService.count();
        long activeRentals = rentalService.countActiveRentals();
        long activeReservations = reservationService.countByStatuses(ReservationStatus.NEW,ReservationStatus.ISSUED);
        long issuedReservations = reservationService.countByStatuses(ReservationStatus.ISSUED,ReservationStatus.ISSUED);
        long newReservations = reservationService.countByStatuses(ReservationStatus.NEW,ReservationStatus.NEW);
        long availableBicycles = bicycleService.countByState(StateBicycle.AVAILABLE);
        long rentedBicycles = bicycleService.countByState(StateBicycle.RENTED);
        long unavailableBicycles = bicycleService.countByState(StateBicycle.UNAVAILABLE);

        LatestRentalInfo latestRental = rentalService.getLatestRentalInfo();
        LatestReservationInfo latestReservation = reservationService.getLatestReservationInfo();
        LatestCustomerInfo latestCustomer = customerService.getLatestCustomerInfo();

        return new ManagerDashboardData(
            String.valueOf(activeRentals),
            String.valueOf(activeReservations),
            String.valueOf(totalCustomers),
            String.valueOf(availableBicycles),
            LocalizationManager.getStringByKey("manager.dashboard.issued_reservations")
                + ": " + issuedReservations,
            LocalizationManager.getStringByKey("manager.dashboard.new_reservations")
                + ": " + newReservations,
            LocalizationManager.getStringByKey("manager.dashboard.rented_bicycles")
                + ": " + rentedBicycles,
            LocalizationManager.getStringByKey("manager.dashboard.unavailable_bicycles")
                + ": " + unavailableBicycles,
            LocalizationManager.getStringByKey("manager.dashboard.total_bicycles")
                + ": " + totalBicycles,
            buildLatestRentalText(latestRental),
            buildLatestReservationText(latestReservation),
            buildLatestCustomerText(latestCustomer)
        );
      }
    };

    task.setOnSucceeded(event -> {
      try {
        ManagerDashboardData data = task.getValue();

        activeRentalsValue.set(data.activeRentalsValue());
        activeReservationsValue.set(data.activeReservationsValue());
        totalCustomersValue.set(data.totalCustomersValue());
        availableBicyclesValue.set(data.availableBicyclesValue());

        issuedReservationsText.set(data.issuedReservationsText());
        newReservationsText.set(data.newReservationsText());
        rentedBicyclesText.set(data.rentedBicyclesText());
        unavailableBicyclesText.set(data.unavailableBicyclesText());
        totalBicyclesText.set(data.totalBicyclesText());

        latestRentalText.set(data.latestRentalText());
        latestReservationText.set(data.latestReservationText());
        latestCustomerText.set(data.latestCustomerText());
      } finally {
        loading.set(false);
      }
    });

    task.setOnFailed(event -> {
      errorText.set(LocalizationManager.getStringByKey("error.dashboard.load"));
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
        "manager.dashboard.latest_rental"
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
        "manager.dashboard.latest_reservation"
    )
        + ": "
        + reservation.customerName()
        + " — "
        + reservation.bicycleModel()
        + " — "
        + reservation.start().format(formatter);
  }

  private String buildLatestCustomerText(
      LatestCustomerInfo customer
  ) {

    if (customer == null) {
      return LocalizationManager.getStringByKey(
          "manager.dashboard.no_data"
      );
    }

    return LocalizationManager.getStringByKey(
        "manager.dashboard.latest_customer"
    )
        + ": "
        + safe(customer.fullName());
  }

  private String safe(String value) {
    return value == null || value.isBlank()
        ? LocalizationManager.getStringByKey("manager.dashboard.no_data")
        : value;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty subtitleTextProperty() {
    return subtitleText;
  }

  public StringProperty activeRentalsTitleProperty() {
    return activeRentalsTitle;
  }

  public StringProperty activeReservationsTitleProperty() {
    return activeReservationsTitle;
  }

  public StringProperty totalCustomersTitleProperty() {
    return totalCustomersTitle;
  }

  public StringProperty availableBicyclesTitleProperty() {
    return availableBicyclesTitle;
  }

  public StringProperty activeRentalsValueProperty() {
    return activeRentalsValue;
  }

  public StringProperty activeReservationsValueProperty() {
    return activeReservationsValue;
  }

  public StringProperty totalCustomersValueProperty() {
    return totalCustomersValue;
  }

  public StringProperty availableBicyclesValueProperty() {
    return availableBicyclesValue;
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

  public StringProperty issuedReservationsTextProperty() {
    return issuedReservationsText;
  }

  public StringProperty newReservationsTextProperty() {
    return newReservationsText;
  }

  public StringProperty rentedBicyclesTextProperty() {
    return rentedBicyclesText;
  }

  public StringProperty unavailableBicyclesTextProperty() {
    return unavailableBicyclesText;
  }

  public StringProperty totalBicyclesTextProperty() {
    return totalBicyclesText;
  }

  public StringProperty latestRentalTextProperty() {
    return latestRentalText;
  }

  public StringProperty latestReservationTextProperty() {
    return latestReservationText;
  }

  public StringProperty latestCustomerTextProperty() {
    return latestCustomerText;
  }

  public StringProperty openCustomersButtonTextProperty() {
    return openCustomersButtonText;
  }

  public StringProperty openRentalsButtonTextProperty() {
    return openRentalsButtonText;
  }

  public StringProperty openReservationsButtonTextProperty() {
    return openReservationsButtonText;
  }

  public BooleanProperty loadingProperty() {
    return loading;
  }

  public StringProperty errorTextProperty() {
    return errorText;
  }

  private record ManagerDashboardData(
      String activeRentalsValue,
      String activeReservationsValue,
      String totalCustomersValue,
      String availableBicyclesValue,
      String issuedReservationsText,
      String newReservationsText,
      String rentedBicyclesText,
      String unavailableBicyclesText,
      String totalBicyclesText,
      String latestRentalText,
      String latestReservationText,
      String latestCustomerText
  ) {
  }
}