package org.example.bicyclesharing.viewModel.manager;

import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.example.bicyclesharing.viewModel.AsyncViewModel;
import org.example.bicyclesharing.viewModel.manager.item.ReservationItemViewModel;

public class ManagerReservationsViewModel extends AsyncViewModel {

  private final ReservationService reservationService;
  private final RentalService rentalService;
  private final CustomerService customerService;
  private final BicycleService bicycleService;

  private final ObservableList<ReservationItemViewModel> reservations =
      FXCollections.observableArrayList();

  private final ObservableList<String> statusFilters =
      FXCollections.observableArrayList();

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("manager.reservations.title");

  private final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("manager.reservations.search");

  private final StringProperty addButtonText =
      LocalizationManager.getStringProperty("manager.reservations.add");

  private final StringProperty issueButtonText =
      LocalizationManager.getStringProperty("manager.reservations.issue");

  private final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("manager.reservations.cancel");

  private final StringProperty editButtonText =
      LocalizationManager.getStringProperty("edit.button");

  private final StringProperty countText =
      new SimpleStringProperty("");

  private final StringProperty searchText =
      new SimpleStringProperty("");

  private final StringProperty statusFilterText =
      new SimpleStringProperty();

  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  public ManagerReservationsViewModel(
      User currentUser,
      ReservationService reservationService,
      RentalService rentalService,
      CustomerService customerService,
      BicycleService bicycleService
  ) {
    super(currentUser);
    this.reservationService = reservationService;
    this.rentalService = rentalService;
    this.customerService = customerService;
    this.bicycleService = bicycleService;

    initializeStatusFilters();
  }

  public void initialize() {
    loadReservationsAsync();
  }

  private void initializeStatusFilters() {
    statusFilters.setAll(
        LocalizationManager.getStringByKey("manager.reservations.filter.all"),
        LocalizationManager.getStringByKey("reservation.status.new"),
        LocalizationManager.getStringByKey("reservation.status.issued"),
        LocalizationManager.getStringByKey("reservation.status.cancelled")
    );

    statusFilterText.set(
        LocalizationManager.getStringByKey("manager.reservations.filter.all")
    );
  }

  public ObservableList<ReservationItemViewModel> getReservations() {
    return reservations;
  }

  public ObservableList<String> getStatusFilters() {
    return statusFilters;
  }

  public void loadReservationsAsync() {
    runAsync(
        () -> {
          reservationService.updateStatuses();
          return reservationService.findByFilters("", null);
        },
        this::setReservations
    );
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    ReservationStatus status = resolveSelectedStatus();

    runAsync(
        () -> {
          reservationService.updateStatuses();
          return reservationService.findByFilters(search, status);
        },
        this::setReservations
    );
  }

  public void refreshAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    ReservationStatus status = resolveSelectedStatus();

    if (search.isBlank() && status == null) {
      loadReservationsAsync();
    } else {
      applyFiltersAsync();
    }
  }

  public void cancelReservation(ReservationItemViewModel item) {
    if (item == null) {
      return;
    }

    cancelReservation(item.getReservation());
  }

  public void issueReservation(ReservationItemViewModel item) {
    if (item == null) {
      return;
    }

    issueReservation(item.getReservation());
  }

  public void validateCanCreateReservation() {
    reservationService.validateCanCreateReservation();
  }

  private void cancelReservation(Reservation reservation) {
    if (reservation == null) {
      return;
    }

    reservation.setStatus(ReservationStatus.CANCELLED);

    Customer customer = customerService.getById(reservation.getCustomerId()).orElse(null);

    if (customer != null) {
      customer.setActiveReservation(null);
      customerService.update(customer);
    }

    reservationService.update(reservation);
    refreshAsync();
  }

  private void issueReservation(Reservation reservation) {
    if (reservation == null) {
      return;
    }

    Bicycle bicycle = bicycleService.getById(reservation.getBicycleId()).orElse(null);
    Customer customer = customerService.getById(reservation.getCustomerId()).orElse(null);

    if (bicycle == null || customer == null) {
      return;
    }

    Rental rental = new Rental(
        reservation.getCustomerId(),
        reservation.getBicycleId()
    );

    reservation.setStatus(ReservationStatus.ISSUED);
    bicycle.setState(StateBicycle.RENTED);
    customer.setActiveRent(rental.getId());
    customer.setActiveReservation(null);

    rentalService.add(rental);
    bicycleService.update(bicycle);
    customerService.update(customer);
    reservationService.update(reservation);

    refreshAsync();
  }

  private void setReservations(List<Reservation> result) {
    reservations.setAll(
        result.stream()
            .map(this::toItemViewModel)
            .toList()
    );

    updateCount();
  }

  private ReservationItemViewModel toItemViewModel(Reservation reservation) {
    return new ReservationItemViewModel(
        reservation,
        getCustomerName(reservation),
        getBicycleText(reservation),
        getPeriodText(reservation),
        getDocumentText(reservation),
        getDepositText(reservation),
        getStatusText(reservation),
        canIssue(reservation),
        canCancel(reservation)
    );
  }

  private String getCustomerName(Reservation reservation) {
    Customer customer = customerService.getById(reservation.getCustomerId()).orElse(null);
    return customer == null ? "-" : customer.getFullName();
  }

  private String getBicycleText(Reservation reservation) {
    Bicycle bicycle = bicycleService.getById(reservation.getBicycleId()).orElse(null);

    return LocalizationManager.getStringByKey("manager.reservations.card.bicycle")
        + ": "
        + (bicycle == null ? "-" : bicycle.getModel());
  }

  private String getPeriodText(Reservation reservation) {
    return LocalizationManager.getStringByKey("manager.reservations.card.period")
        + ": "
        + getStartText(reservation)
        + " - "
        + getEndText(reservation);
  }

  private String getStartText(Reservation reservation) {
    return reservation.getStartTime() == null
        ? "-"
        : reservation.getStartTime().format(formatter);
  }

  private String getEndText(Reservation reservation) {
    return reservation.getEndTime() == null
        ? "-"
        : reservation.getEndTime().format(formatter);
  }

  private String getDocumentText(Reservation reservation) {
    String documentType = reservation.getDocumentType() == null
        ? "-"
        : LocalizationManager.getStringByKey(reservation.getDocumentType().getKey());

    return LocalizationManager.getStringByKey("manager.reservations.card.document")
        + ": "
        + documentType
        + ": "
        + reservation.getDocumentNumber();
  }

  private String getDepositText(Reservation reservation) {
    return LocalizationManager.getStringByKey("manager.reservations.card.deposit")
        + ": "
        + reservation.getDepositAmount()
        + " "
        + LocalizationManager.getStringByKey("label.currency");
  }

  private String getStatusText(Reservation reservation) {
    return reservation.getStatus() == null
        ? "-"
        : LocalizationManager.getStringByKey(reservation.getStatus().getKey());
  }

  private boolean canIssue(Reservation reservation) {
    return reservation != null
        && reservation.getStatus() == ReservationStatus.NEW;
  }

  private boolean canCancel(Reservation reservation) {
    return reservation != null
        && reservation.getStatus() != ReservationStatus.CANCELLED
        && reservation.getStatus() != ReservationStatus.ISSUED;
  }

  private ReservationStatus resolveSelectedStatus() {
    String statusText = statusFilterText.get();

    if (statusText == null
        || statusText.equals(LocalizationManager.getStringByKey("manager.reservations.filter.all"))) {
      return null;
    }

    for (ReservationStatus status : ReservationStatus.values()) {
      String localizedStatus = LocalizationManager.getStringByKey(status.getKey());

      if (localizedStatus.equals(statusText)) {
        return status;
      }
    }

    return null;
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("manager.reservations.count")
            + ": "
            + reservations.size()
    );
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
  }

  public StringProperty addButtonTextProperty() {
    return addButtonText;
  }

  public StringProperty issueButtonTextProperty() {
    return issueButtonText;
  }

  public StringProperty cancelButtonTextProperty() {
    return cancelButtonText;
  }

  public StringProperty editButtonTextProperty() {
    return editButtonText;
  }

  public StringProperty countTextProperty() {
    return countText;
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }

  public StringProperty statusFilterTextProperty() {
    return statusFilterText;
  }
}