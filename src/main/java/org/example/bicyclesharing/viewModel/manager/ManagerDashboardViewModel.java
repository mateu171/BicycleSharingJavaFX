package org.example.bicyclesharing.viewModel.manager;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("manager.dashboard.title");
  public final StringProperty subtitleText =
      LocalizationManager.getStringProperty("manager.dashboard.subtitle");

  public final StringProperty activeRentalsTitle =
      LocalizationManager.getStringProperty("manager.dashboard.active_rentals");
  public final StringProperty activeReservationsTitle =
      LocalizationManager.getStringProperty("manager.dashboard.active_reservations");
  public final StringProperty totalCustomersTitle =
      LocalizationManager.getStringProperty("manager.dashboard.total_customers");
  public final StringProperty availableBicyclesTitle =
      LocalizationManager.getStringProperty("manager.dashboard.available_bicycles");

  public final StringProperty activeRentalsValue = new SimpleStringProperty("0");
  public final StringProperty activeReservationsValue = new SimpleStringProperty("0");
  public final StringProperty totalCustomersValue = new SimpleStringProperty("0");
  public final StringProperty availableBicyclesValue = new SimpleStringProperty("0");

  public final StringProperty attentionTitle =
      LocalizationManager.getStringProperty("manager.dashboard.attention_title");
  public final StringProperty latestActivityTitle =
      LocalizationManager.getStringProperty("manager.dashboard.latest_activity_title");
  public final StringProperty quickActionsTitle =
      LocalizationManager.getStringProperty("manager.dashboard.quick_actions_title");

  public final StringProperty issuedReservationsText = new SimpleStringProperty();
  public final StringProperty newReservationsText = new SimpleStringProperty();
  public final StringProperty rentedBicyclesText = new SimpleStringProperty();
  public final StringProperty unavailableBicyclesText = new SimpleStringProperty();
  public final StringProperty totalBicyclesText = new SimpleStringProperty();

  public final StringProperty latestRentalText = new SimpleStringProperty();
  public final StringProperty latestReservationText = new SimpleStringProperty();
  public final StringProperty latestCustomerText = new SimpleStringProperty();

  public final StringProperty openCustomersButtonText =
      LocalizationManager.getStringProperty("manager.dashboard.open_customers");
  public final StringProperty openRentalsButtonText =
      LocalizationManager.getStringProperty("manager.dashboard.open_rentals");
  public final StringProperty openReservationsButtonText =
      LocalizationManager.getStringProperty("manager.dashboard.open_reservations");

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

    load();
  }

  public void load() {
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

    activeRentalsValue.set(String.valueOf(activeRentals));
    activeReservationsValue.set(String.valueOf(activeReservations));
    totalCustomersValue.set(String.valueOf(customers.size()));
    availableBicyclesValue.set(String.valueOf(availableBicycles));

    issuedReservationsText.set(
        LocalizationManager.getStringByKey("manager.dashboard.issued_reservations") + ": " + issuedReservations
    );
    newReservationsText.set(
        LocalizationManager.getStringByKey("manager.dashboard.new_reservations") + ": " + newReservations
    );
    rentedBicyclesText.set(
        LocalizationManager.getStringByKey("manager.dashboard.rented_bicycles") + ": " + rentedBicycles
    );
    unavailableBicyclesText.set(
        LocalizationManager.getStringByKey("manager.dashboard.unavailable_bicycles") + ": " + unavailableBicycles
    );
    totalBicyclesText.set(
        LocalizationManager.getStringByKey("manager.dashboard.total_bicycles") + ": " + bicycles.size()
    );

    latestRentalText.set(buildLatestRentalText(rentals));
    latestReservationText.set(buildLatestReservationText(reservations));
    latestCustomerText.set(buildLatestCustomerText(customers));
  }

  private String buildLatestRentalText(List<Rental> rentals) {
    return rentals.stream()
        .sorted(Comparator.comparing(Rental::getStart, Comparator.nullsLast(Comparator.reverseOrder())))
        .findFirst()
        .map(rental ->
            LocalizationManager.getStringByKey("manager.dashboard.latest_rental")
                + ": "
                + getCustomerName(rental.getCustomerId())
                + " — "
                + getBicycleModel(rental.getBicycleId())
                + " — "
                + formatDate(rental.getStart())
        )
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.no_data"));
  }

  private String buildLatestReservationText(List<Reservation> reservations) {
    return reservations.stream()
        .sorted(Comparator.comparing(Reservation::getStartTime, Comparator.nullsLast(Comparator.reverseOrder())))
        .findFirst()
        .map(reservation ->
            LocalizationManager.getStringByKey("manager.dashboard.latest_reservation")
                + ": "
                + getCustomerName(reservation.getCustomerId())
                + " — "
                + getBicycleModel(reservation.getBicycleId())
                + " — "
                + formatDate(reservation.getStartTime())
        )
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.no_data"));
  }

  private String buildLatestCustomerText(List<Customer> customers) {
    return customers.stream()
        .sorted(Comparator.comparing(Customer::getFullName, Comparator.nullsLast(String::compareToIgnoreCase)))
        .reduce((first, second) -> second)
        .map(customer ->
            LocalizationManager.getStringByKey("manager.dashboard.latest_customer")
                + ": "
                + safe(customer.getFullName())
        )
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.no_data"));
  }

  private String getCustomerName(UUID customerId) {
    if (customerId == null) {
      return LocalizationManager.getStringByKey("manager.dashboard.unknown_customer");
    }

    return customerService.getAll().stream()
        .filter(customer -> customerId.equals(customer.getId()))
        .map(Customer::getFullName)
        .findFirst()
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.unknown_customer"));
  }

  private String getBicycleModel(UUID bicycleId) {
    if (bicycleId == null) {
      return LocalizationManager.getStringByKey("manager.dashboard.unknown_bicycle");
    }

    return bicycleService.getAll().stream()
        .filter(bicycle -> bicycleId.equals(bicycle.getId()))
        .map(Bicycle::getModel)
        .findFirst()
        .orElse(LocalizationManager.getStringByKey("manager.dashboard.unknown_bicycle"));
  }

  private String formatDate(java.time.LocalDateTime value) {
    return value == null
        ? LocalizationManager.getStringByKey("manager.dashboard.no_data")
        : value.format(formatter);
  }

  private String safe(String value) {
    return value == null || value.isBlank()
        ? LocalizationManager.getStringByKey("manager.dashboard.no_data")
        : value;
  }
}