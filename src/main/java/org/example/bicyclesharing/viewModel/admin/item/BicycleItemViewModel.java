package org.example.bicyclesharing.viewModel.admin.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.util.LocalizationManager;

public class BicycleItemViewModel {

  private final Bicycle bicycle;

  private final StringProperty modelText = new SimpleStringProperty();
  private final StringProperty priceText = new SimpleStringProperty();
  private final StringProperty stateTitleText = new SimpleStringProperty();
  private final StringProperty stateText = new SimpleStringProperty();
  private final StringProperty imagePath = new SimpleStringProperty();

  public BicycleItemViewModel(Bicycle bicycle)
  {
    this.bicycle = bicycle;

    modelText.set(bicycle.getModel());
    priceText.set(LocalizationManager.getStringByKey("admin.bicycles.price")
    + ": "
    + String.format("%.2f",bicycle.getPricePerMinute()));

    stateTitleText.set(LocalizationManager.getStringByKey("admin.bicycles.state"));
    stateText.set(LocalizationManager.getStringByKey(bicycle.getState().getKey()));
    imagePath.set(bicycle.getImagePath());
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

  public StringProperty stateTitleTextProperty() {
    return stateTitleText;
  }

  public StringProperty stateTextProperty() {
    return stateText;
  }

  public StringProperty imagePathProperty() {
    return imagePath;
  }

}
