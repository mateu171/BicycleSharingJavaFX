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
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class ManagerActiveRentalsViewModel extends BaseViewModel {

  private final RentalService rentalService;
  private final CustomerService customerService;
  private final BicycleService bicycleService;

  private final ObservableList<Rental> rentals = FXCollections.observableArrayList();

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("manager.rentals.active.title");
  public final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("manager.rentals.active.search");
  public final StringProperty finishButtonText =
      LocalizationManager.getStringProperty("manager.rentals.active.finish");
  public final StringProperty countText = new SimpleStringProperty("");
  public final StringProperty searchText = new SimpleStringProperty("");

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  public ManagerActiveRentalsViewModel(User currentUser,
      RentalService rentalService,
      CustomerService customerService,
      BicycleService bicycleService) {
    super(currentUser);
    this.rentalService = rentalService;
    this.customerService = customerService;
    this.bicycleService = bicycleService;
    loadRentals();
  }

  public ObservableList<Rental> getRentals() {
    return rentals;
  }

  public void loadRentals() {
    rentals.setAll(
        rentalService.getAll().stream()
            .filter(r -> r.getEnd() == null)
            .collect(Collectors.toList())
    );
    updateCount();
  }

  public void applyFilters() {
    List<Rental> filtered = rentalService.getAll().stream()
        .filter(r -> r.getEnd() == null)
        .filter(r -> {
          String search = searchText.get() == null ? "" : searchText.get().trim().toLowerCase(Locale.ROOT);
          if (search.isEmpty()) {
            return true;
          }

          return getCustomerName(r).toLowerCase(Locale.ROOT).contains(search)
              || getBicycleModel(r).toLowerCase(Locale.ROOT).contains(search);
        })
        .collect(Collectors.toList());

    rentals.setAll(filtered);
    updateCount();
  }

  private void updateCount() {
    countText.set(LocalizationManager.getStringByKey("manager.rentals.active.count") + ": " + rentals.size());
  }

  public String getCustomerName(Rental rental) {
    Customer customer = customerService.getById(rental.getCustomerId()).orElse(null);
    return customer != null ? customer.getFullName() : "-";
  }

  public String getBicycleModel(Rental rental) {
    Bicycle bicycle = bicycleService.getById(rental.getBicycleId()).orElse(null);
    return bicycle != null ? bicycle.getModel() : "-";
  }

  public String getStartText(Rental rental) {
    return rental.getStart() != null ? rental.getStart().format(formatter) : "-";
  }
}