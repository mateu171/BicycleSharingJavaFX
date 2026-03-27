package org.example.bicyclesharing.viewModel.admin.modalViewModal;

import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditBicycleViewModel {

  private final BicycleService bicycleService;
  private final Bicycle editingBicycle;

  public final StringProperty titleText = new SimpleStringProperty();
  public final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  public final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  public final StringProperty modelLabelText =
      LocalizationManager.getStringProperty("admin.bicycles.model");
  public final StringProperty typeLabelText =
      LocalizationManager.getStringProperty("admin.bicycles.type");
  public final StringProperty priceLabelText =
      LocalizationManager.getStringProperty("admin.bicycles.price");
  public final StringProperty latitudeLabelText =
      LocalizationManager.getStringProperty("admin.bicycles.latitude");
  public final StringProperty longitudeLabelText =
      LocalizationManager.getStringProperty("admin.bicycles.longitude");

  public final StringProperty model = new SimpleStringProperty("");
  public final StringProperty price = new SimpleStringProperty("");
  public final StringProperty latitude = new SimpleStringProperty("");
  public final StringProperty longitude = new SimpleStringProperty("");

  public TypeBicycle selectedType;

  public final StringProperty modelError = new SimpleStringProperty("");
  public final StringProperty typeError = new SimpleStringProperty("");
  public final StringProperty priceError = new SimpleStringProperty("");
  public final StringProperty latitudeError = new SimpleStringProperty("");
  public final StringProperty longitudeError = new SimpleStringProperty("");

  public AddEditBicycleViewModel(BicycleService bicycleService, Bicycle editingBicycle) {
    this.bicycleService = bicycleService;
    this.editingBicycle = editingBicycle;

    if (editingBicycle == null) {
      titleText.set(LocalizationManager.getStringByKey("admin.bicycles.add.title"));
      selectedType = TypeBicycle.URBAN;
    } else {
      titleText.set(LocalizationManager.getStringByKey("admin.bicycles.edit.title"));
      selectedType = editingBicycle.getTypeBicycle();
    }
  }

  public boolean isEditMode() {
    return editingBicycle != null;
  }

  public boolean save() {
    clearErrors();

    try {
      Bicycle bicycle;

      if (editingBicycle == null) {
        double lat = parseLatitude(latitude.get());
        double lon = parseLongitude(longitude.get());

        bicycle = new Bicycle(
            model.get(),
            price.get(),
            lat,
            lon
        );
        bicycle.setTypeBicycle(selectedType);
        bicycleService.add(bicycle);
      } else {
        String modelValue = isBlank(model.get()) ? editingBicycle.getModel() : model.get().trim();
        String priceValue = isBlank(price.get())
            ? String.valueOf(editingBicycle.getPricePerMinute())
            : price.get().trim();

        double latValue = isBlank(latitude.get())
            ? editingBicycle.getLatitude()
            : parseLatitude(latitude.get());

        double lonValue = isBlank(longitude.get())
            ? editingBicycle.getLongitude()
            : parseLongitude(longitude.get());

        TypeBicycle typeValue = selectedType == null
            ? editingBicycle.getTypeBicycle()
            : selectedType;

        Bicycle validated = new Bicycle(modelValue, priceValue, latValue, lonValue);
        validated.setTypeBicycle(typeValue);

        editingBicycle.setModel(validated.getModel());
        editingBicycle.setPricePerMinute(String.valueOf(validated.getPricePerMinute()));
        editingBicycle.setLatitude(validated.getLatitude());
        editingBicycle.setLongitude(validated.getLongitude());
        editingBicycle.setTypeBicycle(typeValue);

        if (!editingBicycle.isValid()) {
          throw new CustomEntityValidationExeption(editingBicycle.getErrors());
        }

        bicycleService.update(editingBicycle);
      }

      return true;
    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        String text = messages.stream()
            .map(LocalizationManager::getStringByKey)
            .collect(Collectors.joining("\n"));

        switch (field) {
          case "model" -> modelError.set(text);
          case "pricePerMinute", "pricePerHour" -> priceError.set(text);
          case "latitude" -> latitudeError.set(text);
          case "longitude" -> longitudeError.set(text);
        }
      });
      return false;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private double parseLatitude(String value) {
    try {
      return Double.parseDouble(value.trim());
    } catch (Exception e) {
      latitudeError.set(LocalizationManager.getStringByKey("bicycle.latitude.invalid"));
      throw new IllegalArgumentException();
    }
  }

  private double parseLongitude(String value) {
    try {
      return Double.parseDouble(value.trim());
    } catch (Exception e) {
      longitudeError.set(LocalizationManager.getStringByKey("bicycle.longitude.invalid"));
      throw new IllegalArgumentException();
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void clearErrors() {
    modelError.set("");
    typeError.set("");
    priceError.set("");
    latitudeError.set("");
    longitudeError.set("");
  }
}