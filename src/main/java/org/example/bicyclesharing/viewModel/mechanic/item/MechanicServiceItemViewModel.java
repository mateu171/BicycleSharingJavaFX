package org.example.bicyclesharing.viewModel.mechanic.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Bicycle;

public class MechanicServiceItemViewModel {

  private final Bicycle bicycle;

  private final StringProperty modelText = new SimpleStringProperty();
  private final StringProperty priceText = new SimpleStringProperty();
  private final StringProperty stateText = new SimpleStringProperty();

  public MechanicServiceItemViewModel(
      Bicycle bicycle,
      String modelText,
      String priceText,
      String stateText
  ) {
    this.bicycle = bicycle;
    this.modelText.set(modelText);
    this.priceText.set(priceText);
    this.stateText.set(stateText);
  }

  public Bicycle getBicycle() {
    return bicycle;
  }

  public StringProperty modelTextProperty() {
    return modelText;
  }

  public StringProperty priceTextProperty() {
    return priceText;
  }

  public StringProperty stateTextProperty() {
    return stateText;
  }
}