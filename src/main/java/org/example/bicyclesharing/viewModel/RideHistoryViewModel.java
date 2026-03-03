package org.example.bicyclesharing.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.RentalService;

public class RideHistoryViewModel {

  private final ObservableList<Rental> rentals =
      FXCollections.observableArrayList();

  private final RentalService rentalService;
  private final User currentUser;

  public RideHistoryViewModel(RentalService rentalService, User currentUser) {
    this.rentalService = rentalService;
    this.currentUser = currentUser;

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
