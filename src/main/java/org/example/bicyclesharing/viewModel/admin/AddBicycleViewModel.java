package org.example.bicyclesharing.viewModel.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddBicycleViewModel {

  private final BicycleService bicycleService;

  public final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  public final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  public final StringProperty model = new SimpleStringProperty("");
  public final StringProperty price = new SimpleStringProperty("");
  public final StringProperty latitude = new SimpleStringProperty("");
  public final StringProperty longitude = new SimpleStringProperty("");

  public final StringProperty modelPromText = new SimpleStringProperty(LocalizationManager.getStringByKey("bicycle.model.prompt"));
  public final StringProperty pricePromText = new SimpleStringProperty(LocalizationManager.getStringByKey("bicycle.price.prompt"));
  public final StringProperty latitudePromText = new SimpleStringProperty(LocalizationManager.getStringByKey("bicycle.latitude.prompt"));
  public final StringProperty longitudePromText = new SimpleStringProperty(LocalizationManager.getStringByKey("bicycle.longitude.prompt"));

  public final StringProperty modelErrorKey = new SimpleStringProperty("");
  public final StringProperty priceErrorKey = new SimpleStringProperty("");
  public final StringProperty latitudeErrorKey = new SimpleStringProperty("");
  public final StringProperty longitudeErrorKey = new SimpleStringProperty("");


  public AddBicycleViewModel(BicycleService bicycleService) {
    this.bicycleService = bicycleService;
  }

  public boolean save() {
    clearErrors();

    double lat;
    double lon;

    try {
      lat = Double.parseDouble(latitude.get());
    } catch (Exception e) {
      latitudeErrorKey.set("bicycle.latitude.invalid");
      return false;
    }

    try {
      lon = Double.parseDouble(longitude.get());
    } catch (Exception e) {
      longitudeErrorKey.set("bicycle.longitude.invalid");
      return false;
    }

    try {
      Bicycle bicycle = new Bicycle(
          model.get(),
          price.get(),
          lat,
          lon
      );

      bicycleService.add(bicycle);
      return true;

    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, keys) -> {
        StringProperty targetProperty;
        switch (field) {
          case "model" -> targetProperty = modelErrorKey;
          case "pricePerHour" -> targetProperty = priceErrorKey;
          case "latitude" -> targetProperty = latitudeErrorKey;
          case "longitude" -> targetProperty = longitudeErrorKey;
          default -> targetProperty = null;
        }
        if (targetProperty != null && !keys.isEmpty()) {
          targetProperty.set(keys.get(0));
        }
      });
      return false;
    }
  }

  private void clearErrors() {
    modelErrorKey.set("");
    priceErrorKey.set("");
    latitudeErrorKey.set("");
    longitudeErrorKey.set("");
  }

}
