package org.example.bicyclesharing.viewModel.manager.item;

import java.time.format.DateTimeFormatter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.util.LocalizationManager;

public class RentalItemViewModel {

  private final Rental rental;

  private final StringProperty customerText = new SimpleStringProperty();
  private final StringProperty bicycleText = new SimpleStringProperty();
  private final StringProperty startText = new SimpleStringProperty();
  private final BooleanProperty active = new SimpleBooleanProperty();

  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  public RentalItemViewModel(
      Rental rental,
      Customer customer,
      Bicycle bicycle
  ) {
    this.rental = rental;

    active.set(rental.getEnd() == null);

    customerText.set(customer == null ? "-" : customer.getFullName());

    bicycleText.set(
        LocalizationManager.getStringByKey("manager.rentals.active.card.bicycle")
            + ": "
            + (bicycle == null ? "-" : bicycle.getModel())
    );

    startText.set(
        LocalizationManager.getStringByKey("manager.rentals.active.card.start")
            + ": "
            + (rental.getStart() == null ? "-" : rental.getStart().format(formatter))
    );
  }

  public Rental getRental() {
    return rental;
  }

  public StringProperty customerTextProperty() {
    return customerText;
  }

  public StringProperty bicycleTextProperty() {
    return bicycleText;
  }

  public StringProperty startTextProperty() {
    return startText;
  }

  public BooleanProperty activeProperty() {
    return active;
  }
}