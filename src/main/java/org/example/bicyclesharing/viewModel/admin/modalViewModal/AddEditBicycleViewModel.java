package org.example.bicyclesharing.viewModel.admin.modalViewModal;

import java.io.File;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.ImageStorageUtil;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditBicycleViewModel {

  private static final String DEFAULT_IMAGE =
      "/org/example/bicyclesharing/art/image/defaultImg.jpg";

  private final BicycleService bicycleService;
  private final StationService stationService;
  private final Bicycle editingBicycle;

  private final StringProperty titleText = new SimpleStringProperty();

  private final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  private final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");
  private final StringProperty uploadButtonText =
      LocalizationManager.getStringProperty("uploadPhoto.button.text");

  private final StringProperty modelLabelText =
      LocalizationManager.getStringProperty("admin.bicycles.model");
  private final StringProperty typeLabelText =
      LocalizationManager.getStringProperty("admin.bicycles.type");
  private final StringProperty priceLabelText =
      LocalizationManager.getStringProperty("admin.bicycles.price");
  private final StringProperty stationLabelText =
      LocalizationManager.getStringProperty("admin.bicycles.station");
  private final StringProperty photoLabelText =
      LocalizationManager.getStringProperty("admin.users.photo");

  private final StringProperty model = new SimpleStringProperty("");
  private final StringProperty price = new SimpleStringProperty("");

  private final ObjectProperty<TypeBicycle> selectedType = new SimpleObjectProperty<>();
  private final ObjectProperty<Station> selectedStation = new SimpleObjectProperty<>();

  private final ObjectProperty<ObservableList<TypeBicycle>> types =
      new SimpleObjectProperty<>(FXCollections.observableArrayList(TypeBicycle.values()));

  private final ObjectProperty<ObservableList<Station>> stations =
      new SimpleObjectProperty<>(FXCollections.observableArrayList());

  private final StringProperty modelError = new SimpleStringProperty("");
  private final StringProperty typeError = new SimpleStringProperty("");
  private final StringProperty priceError = new SimpleStringProperty("");
  private final StringProperty stationError = new SimpleStringProperty("");
  private final StringProperty photoError = new SimpleStringProperty("");

  private final StringProperty photoFileNameText =
      LocalizationManager.getStringProperty("file.not.selected");

  private final StringProperty photoPreviewPath = new SimpleStringProperty(DEFAULT_IMAGE);

  private File selectedPhotoFile;
  private String imagePath;

  public AddEditBicycleViewModel(
      BicycleService bicycleService,
      StationService stationService,
      Bicycle editingBicycle
  ) {
    this.bicycleService = bicycleService;
    this.stationService = stationService;
    this.editingBicycle = editingBicycle;
  }

  public void initialize() {
    stations.get().setAll(stationService.getAll());

    if (isEditMode()) {
      initializeEditMode();
    } else {
      initializeAddMode();
    }
  }

  private void initializeAddMode() {
    titleText.set(LocalizationManager.getStringByKey("admin.bicycles.add.title"));
    selectedType.set(TypeBicycle.URBAN);

    if (!stations.get().isEmpty()) {
      selectedStation.set(stations.get().get(0));
    }

    photoPreviewPath.set(DEFAULT_IMAGE);
  }

  private void initializeEditMode() {
    titleText.set(LocalizationManager.getStringByKey("admin.bicycles.edit.title"));

    model.set(editingBicycle.getModel());
    price.set(String.valueOf(editingBicycle.getPricePerMinute()));
    selectedType.set(editingBicycle.getTypeBicycle());

    stations.get().stream()
        .filter(station -> station.getId().equals(editingBicycle.getStationId()))
        .findFirst()
        .ifPresent(selectedStation::set);

    if (editingBicycle.getImagePath() != null && !editingBicycle.getImagePath().isBlank()) {
      photoPreviewPath.set(editingBicycle.getImagePath());
      photoFileNameText.set(new File(editingBicycle.getImagePath()).getName());
    } else {
      photoPreviewPath.set(DEFAULT_IMAGE);
    }
  }

  public boolean save() {
    clearErrors();

    saveSelectedPhotoIfNeeded();

    try {
      if (isEditMode()) {
        updateBicycle();
      } else {
        createBicycle();
      }

      return true;
    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;
    }
  }

  private void createBicycle() {
    Bicycle bicycle = new Bicycle(
        model.get(),
        selectedType.get(),
        price.get(),
        selectedStation.get() == null ? null : selectedStation.get().getId()
    );

    bicycle.setImagePath(imagePath);

    if (selectedStation.get() != null) {
      selectedStation.get().addBicycleId(bicycle.getId());
      stationService.update(selectedStation.get());
    }

    bicycleService.add(bicycle);
  }

  private void updateBicycle() {
    bicycleService.validateCanEdit(editingBicycle);

    UUID oldStationId = editingBicycle.getStationId();
    UUID newStationId = selectedStation.get() == null ? null : selectedStation.get().getId();

    Bicycle validated = new Bicycle(
        model.get(),
        selectedType.get(),
        price.get(),
        newStationId
    );

    editingBicycle.setModel(validated.getModel());
    editingBicycle.setTypeBicycle(validated.getTypeBicycle());
    editingBicycle.setPricePerMinute(String.valueOf(validated.getPricePerMinute()));
    editingBicycle.setStationId(newStationId);
    editingBicycle.setImagePath(resolveFinalImagePath());

    updateStationRelations(oldStationId, newStationId);
    bicycleService.update(editingBicycle);
  }

  private void updateStationRelations(UUID oldStationId, UUID newStationId) {
    if (oldStationId != null && !oldStationId.equals(newStationId)) {
      Station oldStation = stationService.getById(oldStationId);
      if (oldStation != null) {
        oldStation.removeBicycleId(editingBicycle.getId());
        stationService.update(oldStation);
      }
    }

    if (newStationId != null && !newStationId.equals(oldStationId)) {
      Station newStation = stationService.getById(newStationId);
      if (newStation != null) {
        newStation.addBicycleId(editingBicycle.getId());
        stationService.update(newStation);
      }
    }
  }

  private boolean saveSelectedPhotoIfNeeded() {
    if (selectedPhotoFile == null) {
      return false;
    }

    try {
      imagePath = ImageStorageUtil.saveImage(selectedPhotoFile, "bicycles");
      return true;
    } catch (Exception e) {
      photoError.set(LocalizationManager.getStringByKey("error.image.save.failed"));
      return true;
    }
  }

  private String resolveFinalImagePath() {
    if (imagePath != null && !imagePath.isBlank()) {
      return imagePath;
    }

    return editingBicycle.getImagePath();
  }

  public void selectPhoto(File file) {
    selectedPhotoFile = file;
    photoFileNameText.set(file.getName());
    photoPreviewPath.set(file.getAbsolutePath());
    photoError.set("");
  }

  private void applyValidationErrors(CustomEntityValidationExeption e) {
    e.getErrors().forEach((field, messages) -> {
      String text = messages.stream()
          .map(LocalizationManager::getStringByKey)
          .collect(Collectors.joining("\n"));

      switch (field) {
        case "model" -> modelError.set(text);
        case "typeBicycle" -> typeError.set(text);
        case "pricePerMinute" -> priceError.set(text);
        case "stationId" -> stationError.set(text);
        case "imagePath" -> photoError.set(text);
      }
    });
  }

  private void clearErrors() {
    modelError.set("");
    typeError.set("");
    priceError.set("");
    stationError.set("");
    photoError.set("");
  }

  public boolean isEditMode() {
    return editingBicycle != null;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty saveButtonTextProperty() {
    return saveButtonText;
  }

  public StringProperty cancelButtonTextProperty() {
    return cancelButtonText;
  }

  public StringProperty uploadButtonTextProperty() {
    return uploadButtonText;
  }

  public StringProperty modelLabelTextProperty() {
    return modelLabelText;
  }

  public StringProperty typeLabelTextProperty() {
    return typeLabelText;
  }

  public StringProperty priceLabelTextProperty() {
    return priceLabelText;
  }

  public StringProperty stationLabelTextProperty() {
    return stationLabelText;
  }

  public StringProperty photoLabelTextProperty() {
    return photoLabelText;
  }

  public StringProperty modelProperty() {
    return model;
  }

  public StringProperty priceProperty() {
    return price;
  }

  public ObjectProperty<TypeBicycle> selectedTypeProperty() {
    return selectedType;
  }

  public ObjectProperty<Station> selectedStationProperty() {
    return selectedStation;
  }

  public ObjectProperty<ObservableList<TypeBicycle>> typesProperty() {
    return types;
  }

  public ObjectProperty<ObservableList<Station>> stationsProperty() {
    return stations;
  }

  public StringProperty modelErrorProperty() {
    return modelError;
  }

  public StringProperty typeErrorProperty() {
    return typeError;
  }

  public StringProperty priceErrorProperty() {
    return priceError;
  }

  public StringProperty stationErrorProperty() {
    return stationError;
  }

  public StringProperty photoErrorProperty() {
    return photoError;
  }

  public StringProperty photoFileNameTextProperty() {
    return photoFileNameText;
  }

  public StringProperty photoPreviewPathProperty() {
    return photoPreviewPath;
  }
}