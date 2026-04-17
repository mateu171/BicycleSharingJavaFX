package org.example.bicyclesharing.viewModel.manager;

import java.time.format.DateTimeFormatter;
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
import org.example.bicyclesharing.viewModel.AsyncViewModel;

public class ManagerActiveRentalsViewModel extends AsyncViewModel {

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

  public ManagerActiveRentalsViewModel(
      User currentUser,
      RentalService rentalService,
      CustomerService customerService,
      BicycleService bicycleService
  ) {
    super(currentUser);
    this.rentalService = rentalService;
    this.customerService = customerService;
    this.bicycleService = bicycleService;
  }

  public ObservableList<Rental> getRentals() {
    return rentals;
  }

  public void loadRentalsAsync() {
    runAsync(
        () -> rentalService.findActiveByFilters(""),
        result -> {
          rentals.setAll(result);
          updateCount();
        }
    );
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();

    runAsync(
        () -> rentalService.findActiveByFilters(search),
        result -> {
          rentals.setAll(result);
          updateCount();
        }
    );
  }

  public void refreshAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();

    if (search.isBlank()) {
      loadRentalsAsync();
    } else {
      applyFiltersAsync();
    }
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("manager.rentals.active.count") + ": " + rentals.size()
    );
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