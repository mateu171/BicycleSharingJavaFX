package org.example.bicyclesharing.viewModel.manager;

import java.time.LocalDateTime;
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
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
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

        List<Rental> rentals = rentalService.getAll();
        List<Reservation> reservations = reservationService.getAll();
        List<Customer> customers = customerService.getAll();
        List<Bicycle> bicycles = bicycleService.getAll();

        long activeRentals = rentals.stream()
            .filter(rental -> rental.getEnd() == null)
            .count();

        long activeReservations = reservations.stream()
            .filter(reservation ->
                reservation.getStatus() == ReservationStatus.NEW
                    || reservation.getStatus() == ReservationStatus.ISSUED)
            .count();

        long issuedReservations = reservations.stream()
            .filter(reservation -> reservation.getStatus() == ReservationStatus.ISSUED)
            .count();

        long newReservations = reservations.stream()
            .filter(reservation -> reservation.getStatus() == ReservationStatus.NEW)
            .count();

        long availableBicycles = bicycles.stream()
            .filter(bicycle -> bicycle.getState() == StateBicycle.AVAILABLE)
            .count();

        long rentedBicycles = bicycles.stream()
            .filter(bicycle -> bicycle.getState() == StateBicycle.RENTED)
            .count();

        long unavailableBicycles = bicycles.stream()
            .filter(bicycle ->
                bicycle.getState() == StateBicycle.UNAVAILABLE
                    || bicycle.getState() == StateBicycle.ON_MAINTENANCE
                    || bicycle.getState() == StateBicycle.NEEDS_INSPECTION)
            .count();

        return new ManagerDashboardData(
            String.valueOf(activeRentals),
            String.valueOf(activeReservations),
            String.valueOf(customers.size()),
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
                + ": " + bicycles.size(),
            buildLatestRentalText(rentals, customers, bicycles),
            buildLatestReservationText(reservations, customers, bicycles),
            buildLatestCustomerText(customers)
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
      List<Rental> rentals,
      List<Customer> customers,
      List<Bicycle> bicycles
  ) {
    return rentals.stream()
        .sorted(Comparator.comparing(
            Rental::getStart,
            Comparator.nullsLast(Comparator.reverseOrder())
        ))
        .findFirst()
        .map(rental ->
            LocalizationManager.getStringByKey("manager.dashboard.latest_rental")
                + ": "
                + getCustomerName(rental.getCustomerId(), customers)
                + " — "
                + getBicycleModel(rental.getBicycleId(), bicycles)
                + " — "
                + formatDate(rental.getStart())
        )
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.no_data"));
  }

  private String buildLatestReservationText(
      List<Reservation> reservations,
      List<Customer> customers,
      List<Bicycle> bicycles
  ) {
    return reservations.stream()
        .sorted(Comparator.comparing(
            Reservation::getStartTime,
            Comparator.nullsLast(Comparator.reverseOrder())
        ))
        .findFirst()
        .map(reservation ->
            LocalizationManager.getStringByKey("manager.dashboard.latest_reservation")
                + ": "
                + getCustomerName(reservation.getCustomerId(), customers)
                + " — "
                + getBicycleModel(reservation.getBicycleId(), bicycles)
                + " — "
                + formatDate(reservation.getStartTime())
        )
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.no_data"));
  }

  private String buildLatestCustomerText(List<Customer> customers) {
    return customers.stream()
        .sorted(Comparator.comparing(
            Customer::getFullName,
            Comparator.nullsLast(String::compareToIgnoreCase)
        ))
        .reduce((first, second) -> second)
        .map(customer ->
            LocalizationManager.getStringByKey("manager.dashboard.latest_customer")
                + ": "
                + safe(customer.getFullName())
        )
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.no_data"));
  }

  private String getCustomerName(UUID customerId, List<Customer> customers) {
    if (customerId == null) {
      return LocalizationManager.getStringByKey("manager.dashboard.unknown_customer");
    }

    return customers.stream()
        .filter(customer -> customerId.equals(customer.getId()))
        .map(Customer::getFullName)
        .findFirst()
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.unknown_customer"));
  }

  private String getBicycleModel(UUID bicycleId, List<Bicycle> bicycles) {
    if (bicycleId == null) {
      return LocalizationManager.getStringByKey("manager.dashboard.unknown_bicycle");
    }

    return bicycles.stream()
        .filter(bicycle -> bicycleId.equals(bicycle.getId()))
        .map(Bicycle::getModel)
        .findFirst()
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.unknown_bicycle"));
  }

  private String formatDate(LocalDateTime value) {
    return value == null
        ? LocalizationManager.getStringByKey("manager.dashboard.no_data")
        : value.format(formatter);
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