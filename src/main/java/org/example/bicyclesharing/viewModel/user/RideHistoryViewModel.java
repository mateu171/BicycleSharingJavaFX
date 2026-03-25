package org.example.bicyclesharing.viewModel.user;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class RideHistoryViewModel extends BaseViewModel {
  public final StringProperty titleText = LocalizationManager.getStringProperty("history.title");
  private final ObservableList<Rental> rentals = FXCollections.observableArrayList();

  private final RentalService rentalService;

  public RideHistoryViewModel(RentalService rentalService, User currentUser) {
    super(currentUser);
    this.rentalService = rentalService;

    load();
  }

  private void load()
  {
    rentals.setAll(rentalService.getByUserId(currentUser.getId()));
  }

  public ObservableList<Rental> getRentals() {
    return rentals;
  }
}
