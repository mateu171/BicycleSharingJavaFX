package org.example.bicyclesharing.viewModel.manager;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
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
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class ManagerReservationsViewModel extends BaseViewModel {

  private final ReservationService reservationService;
  private final RentalService rentalService;
  private final CustomerService customerService;
  private final BicycleService bicycleService;

  private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("manager.reservations.title");
  public final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("manager.reservations.search");
  public final StringProperty addButtonText =
      LocalizationManager.getStringProperty("manager.reservations.add");
  public final StringProperty issueButtonText =
      LocalizationManager.getStringProperty("manager.reservations.issue");
  public final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("manager.reservations.cancel");
  public final StringProperty countText = new SimpleStringProperty("");
  public final StringProperty searchText = new SimpleStringProperty("");
  public final StringProperty statusFilterText = new SimpleStringProperty(
      LocalizationManager.getStringByKey("manager.reservations.filter.all")
  );

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  public ManagerReservationsViewModel(User currentUser,
      ReservationService reservationService, RentalService rentalService,
      CustomerService customerService,
      BicycleService bicycleService) {
    super(currentUser);
    this.reservationService = reservationService;
    this.rentalService = rentalService;
    this.customerService = customerService;
    this.bicycleService = bicycleService;
    loadReservations();
  }

  public ObservableList<Reservation> getReservations() {
    return reservations;
  }

  public void loadReservations() {
    reservations.setAll(reservationService.getAll());
    updateCount();
  }

  public void applyFilters() {
    List<Reservation> allReservations = reservationService.getAll();

    String search = searchText.get() == null ? "" : searchText.get().trim().toLowerCase(Locale.ROOT);
    String status = statusFilterText.get();

    List<Reservation> filtered = allReservations.stream()
        .filter(reservation -> {
          boolean matchesSearch =
              search.isEmpty()
                  || getCustomerName(reservation).toLowerCase(Locale.ROOT).contains(search);
          boolean matchesStatus =
              status == null
                  || status.equals(LocalizationManager.getStringByKey("manager.reservations.filter.all"))
                  || getStatusText(reservation).equals(status);

          return matchesSearch && matchesStatus;
        })
        .collect(Collectors.toList());

    reservations.setAll(filtered);
    updateCount();
  }

  private void updateCount() {
    countText.set(LocalizationManager.getStringByKey("manager.reservations.count") + ": " + reservations.size());
  }

  public String getCustomerName(Reservation reservation) {
    Customer customer = customerService.getById(reservation.getCustomerId()).orElse(null);
    return customer != null ? customer.getFullName() : "-";
  }

  public String getBicycleModel(Reservation reservation) {
    Bicycle bicycle = bicycleService.getById(reservation.getBicycleId()).orElse(null);
    return bicycle != null ? bicycle.getModel() : "-";
  }

  public String getStartText(Reservation reservation) {
    return reservation.getStartTime() != null ? reservation.getStartTime().format(formatter) : "-";
  }

  public String getEndText(Reservation reservation) {
    return reservation.getEndTime() != null ? reservation.getEndTime().format(formatter) : "-";
  }

  public String getDocumentText(Reservation reservation) {
    String docType = LocalizationManager.getStringByKey(reservation.getDocumentType().getKey());
    return docType + ": " + reservation.getDocumentNumber();
  }

  public String getDepositText(Reservation reservation) {
    return reservation.getDepositAmount() + " " + LocalizationManager.getStringByKey("label.currency");
  }

  public String getStatusText(Reservation reservation) {
    return LocalizationManager.getStringByKey(reservation.getStatus().getKey());
  }

  public void cancelReservation(Reservation reservation) {
    if (reservation == null) {
      return;
    }

    reservation.setStatus(ReservationStatus.CANCELLED);
    reservationService.update(reservation);
    applyFilters();
  }
  public void issueReservation(Reservation reservation) {
    if (reservation == null) {
      return;
    }

    Bicycle currentBicycle = bicycleService.getById(reservation.getBicycleId()).orElse(null);
    Customer currentCustomer = customerService.getById(reservation.getCustomerId()).orElse(null);

    if (currentBicycle == null || currentCustomer == null) {
      return;
    }

    Rental rental = new Rental(reservation.getCustomerId(), reservation.getBicycleId());
    reservation.setStatus(ReservationStatus.ISSUED);
    currentBicycle.setState(StateBicycle.RENTED);
    currentCustomer.setActiveRent(rental.getId());

    rentalService.add(rental);
    bicycleService.update(currentBicycle);
    customerService.update(currentCustomer);
    reservationService.update(reservation);

    applyFilters();
  }

  public boolean canIssue(Reservation reservation) {
    return reservation != null
        && (reservation.getStatus() == ReservationStatus.NEW
        || reservation.getStatus() == ReservationStatus.CONFIRMED);
  }

  public boolean canCancel(Reservation reservation) {
    return reservation != null
        && reservation.getStatus() != ReservationStatus.CANCELLED
        && reservation.getStatus() != ReservationStatus.COMPLETED
        && reservation.getStatus() != ReservationStatus.ISSUED;
  }
}