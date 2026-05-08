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
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;
import org.example.bicyclesharing.viewModel.manager.item.ActiveRentalItemViewModel;

public class ManagerActiveRentalsViewModel extends AsyncViewModel {

  private final RentalService rentalService;
  private final CustomerService customerService;
  private final BicycleService bicycleService;

  private final ObservableList<ActiveRentalItemViewModel> rentals =
      FXCollections.observableArrayList();

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("manager.rentals.active.title");

  private final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("manager.rentals.active.search");

  private final StringProperty finishButtonText =
      LocalizationManager.getStringProperty("manager.rentals.active.finish");

  private final StringProperty countText =
      new SimpleStringProperty("");

  private final StringProperty searchText =
      new SimpleStringProperty("");


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

  public void initialize()
  {
    loadRentalsAsync();
  }
  public ObservableList<ActiveRentalItemViewModel> getRentals() {
    return rentals;
  }

  private void loadRentalsAsync() {
    runAsync(
        () -> rentalService.findActiveByFilters(""),
        this::setRentals
    );
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();

    runAsync(
        () -> rentalService.findActiveByFilters(search),
        this::setRentals
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

  private void setRentals(List<Rental> result)
  {
    rentals.setAll(result.stream().map(this::toItemViewModel).toList());
    updateCount();
  }

  private ActiveRentalItemViewModel toItemViewModel(Rental rental)
  {
    Customer customer = customerService.getById(rental.getCustomerId()).orElse(null);
    Bicycle bicycle = bicycleService.getById(rental.getBicycleId()).orElse(null);

    return new ActiveRentalItemViewModel(rental,customer,bicycle);
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("manager.rentals.active.count") + ": " + rentals.size()
    );
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
  }

  public StringProperty finishButtonTextProperty() {
    return finishButtonText;
  }

  public StringProperty countTextProperty() {
    return countText;
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }
}