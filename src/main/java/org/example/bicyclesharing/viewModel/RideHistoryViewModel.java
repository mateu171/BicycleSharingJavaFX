package org.example.bicyclesharing.viewModel;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.RentalService;

public class RideHistoryViewModel {

  private final ObservableList<Rental> rentals =
      FXCollections.observableArrayList();

  private final RentalService rentalService;
  private final User currenyUser;

  public RideHistoryViewModel(RentalService rentalService, User currenyUser) {
    this.rentalService = rentalService;
    this.currenyUser = currenyUser;

    load();
  }

  private void load()
  {
    rentals.setAll(rentalService.getByUserId(currenyUser.getId()));
  }

  public ObservableList<Rental> getRentals() {
    return rentals;
  }
}
