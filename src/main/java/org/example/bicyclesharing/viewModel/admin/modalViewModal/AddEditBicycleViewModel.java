package org.example.bicyclesharing.viewModel.admin.modalViewModal;

import java.util.UUID;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditBicycleViewModel {

  private final BicycleService bicycleService;
  private final StationService stationService;
  private final Bicycle editingBicycle;

  public final StringProperty titleText = new SimpleStringProperty();
  public final StringProperty saveButtonText = LocalizationManager.getStringProperty("save.button");
  public final StringProperty cancelButtonText = LocalizationManager.getStringProperty("cancel.button");
  public final StringProperty uploadButtonText = LocalizationManager.getStringProperty("uploadPhoto.button.text");

  public final StringProperty modelLabelText = LocalizationManager.getStringProperty("admin.bicycles.model");
  public final StringProperty typeLabelText = LocalizationManager.getStringProperty("admin.bicycles.type");
  public final StringProperty priceLabelText = LocalizationManager.getStringProperty("admin.bicycles.price");
  public final StringProperty stationLabelText = LocalizationManager.getStringProperty("admin.bicycles.station");
  public final StringProperty photoLabelText = LocalizationManager.getStringProperty("admin.users.photo");
  public final StringProperty photoFileNameText = LocalizationManager.getStringProperty("file.not.selected");

  public final StringProperty model = new SimpleStringProperty("");
  public final StringProperty price = new SimpleStringProperty("");

  public final StringProperty modelError = new SimpleStringProperty("");
  public final StringProperty typeError = new SimpleStringProperty("");
  public final StringProperty priceError = new SimpleStringProperty("");
  public final StringProperty stationError = new SimpleStringProperty("");
  public final StringProperty photoError = new SimpleStringProperty("");

  public final ObservableList<Station> stations = FXCollections.observableArrayList();

  public TypeBicycle selectedType;
  public Station selectedStation;
  private String imagePath;

  public AddEditBicycleViewModel(
      BicycleService bicycleService,
      StationService stationService,
      Bicycle editingBicycle
  ) {
    this.bicycleService = bicycleService;
    this.stationService = stationService;
    this.editingBicycle = editingBicycle;

    stations.setAll(stationService.getAll());

    if (!stations.isEmpty()) {
      selectedStation = stations.get(0);
    }

    if (editingBicycle == null) {
      titleText.set(LocalizationManager.getStringByKey("admin.bicycles.add.title"));
      selectedType = TypeBicycle.URBAN;
    } else {
      titleText.set(LocalizationManager.getStringByKey("admin.bicycles.edit.title"));
      selectedType = editingBicycle.getTypeBicycle();

      stations.stream()
          .filter(station -> station.getId().equals(editingBicycle.getStationId()))
          .findFirst()
          .ifPresent(station -> selectedStation = station);
    }
  }

  public boolean isEditMode() {
    return editingBicycle != null;
  }

  public boolean save() {
    clearErrors();

    try {
      if (editingBicycle == null) {
        Bicycle bicycle = new Bicycle(
            model.get(),
            selectedType,
            price.get(),
            selectedStation == null ? null : selectedStation.getId()
        );

        if(selectedStation != null) {
          selectedStation.addBicycleId(bicycle.getId());
          stationService.update(selectedStation);
        }
        bicycle.setImagePath(imagePath);
        bicycleService.add(bicycle);
      } else {
        UUID oldStationId = editingBicycle.getStationId();
        UUID newStationId = selectedStation == null ? null : selectedStation.getId();

        String modelValue = isBlank(model.get())
            ? editingBicycle.getModel()
            : model.get().trim();

        String priceValue = isBlank(price.get())
            ? String.valueOf(editingBicycle.getPricePerMinute())
            : price.get().trim();

        TypeBicycle typeValue = selectedType == null
            ? editingBicycle.getTypeBicycle()
            : selectedType;

        String finalImagePath = (imagePath != null && !imagePath.trim().isEmpty())
            ? imagePath
            : editingBicycle.getImagePath();

        Bicycle validated = new Bicycle(
            modelValue,
            typeValue,
            priceValue,
            newStationId
        );

        editingBicycle.setModel(validated.getModel());
        editingBicycle.setTypeBicycle(typeValue);
        editingBicycle.setPricePerMinute(String.valueOf(validated.getPricePerMinute()));
        editingBicycle.setStationId(newStationId);
        editingBicycle.setImagePath(finalImagePath);

        if (oldStationId != null && newStationId != null) {

          if (!oldStationId.equals(newStationId)) {

            Station oldStation = stationService.getById(oldStationId);
            Station newStation = stationService.getById(newStationId);

            if (oldStation != null) {
              oldStation.removeBicycleId(editingBicycle.getId());
              stationService.update(oldStation);
            }

            if (newStation != null) {
              newStation.addBicycleId(editingBicycle.getId());
              stationService.update(newStation);
            }
          }

        } else if (oldStationId == null && newStationId != null) {

          Station newStation = stationService.getById(newStationId);

          if (newStation != null) {
            newStation.addBicycleId(editingBicycle.getId());
            stationService.update(newStation);
          }
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
          case "typeBicycle" -> typeError.set(text);
          case "pricePerMinute" -> priceError.set(text);
          case "image_path" -> photoError.set(text);
        }
      });
      return false;
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void clearErrors() {
    modelError.set("");
    typeError.set("");
    priceError.set("");
    stationError.set("");
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public void setPhotoError(String message) {
    photoError.set(message);
  }
}