package org.example.bicyclesharing.viewModel.admin.modalViewModal;

import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditStationViewModel {

  private final StationService stationService;
  private final UserService userService;
  private final Station editingStation;

  private final StringProperty titleText = new SimpleStringProperty();
  private final StringProperty saveButtonText = LocalizationManager.getStringProperty("save.button");
  private final StringProperty cancelButtonText = LocalizationManager.getStringProperty("cancel.button");
  private final StringProperty pickOnMapButtonText = LocalizationManager.getStringProperty("station.pick.on.map");
  private final StringProperty nameLabelText = LocalizationManager.getStringProperty("admin.stations.name");
  private final StringProperty managerLabelText = LocalizationManager.getStringProperty("admin.manger");

  private final ObjectProperty<User> selectedManager = new SimpleObjectProperty<>();
  private final ObjectProperty<ObservableList<User>> managers = new SimpleObjectProperty<>(FXCollections.observableArrayList());

  private final StringProperty name = new SimpleStringProperty("");
  private final StringProperty latitude = new SimpleStringProperty("");
  private final StringProperty longitude = new SimpleStringProperty("");
  private final StringProperty locationInfo = new SimpleStringProperty("");

  private final StringProperty nameError = new SimpleStringProperty("");
  private final StringProperty coordinatesError = new SimpleStringProperty("");
  private final StringProperty managerError = new SimpleStringProperty("");

  public AddEditStationViewModel(
      StationService stationService, UserService userService,
      Station editingStation
  ) {
    this.stationService = stationService;
    this.userService = userService;
    this.editingStation = editingStation;
  }

  public void initialize() {
    managers.get().setAll(userService.getByRole(Role.MANAGER));

    if (isEditMode()) {
      initializeEditMode();
    } else {
      initializeAddMode();
    }
  }

  private void initializeAddMode() {
    titleText.set(LocalizationManager.getStringByKey("admin.stations.add.title"));
    locationInfo.set(LocalizationManager.getStringByKey("station.location.not.selected"));

    if (!managers.get().isEmpty()) {
      selectedManager.set(managers.get().get(0));
    }
  }

  private void initializeEditMode() {
    titleText.set(LocalizationManager.getStringByKey("admin.stations.edit.title"));

    name.set(editingStation.getName());
    setCoordinates(editingStation.getLatitude(), editingStation.getLongitude());
    selectedManager.set(
        managers.get().stream()
            .filter(u -> u.getId().equals(editingStation.getManagerId()))
            .findFirst()
            .orElse(null)
    );
  }

  public boolean isEditMode() {
    return editingStation != null;
  }

  public boolean save() {
    clearErrors();
    try {
      if (isEditMode()) {
        updateStation();
      } else {
        createStation();
      }

      return true;

    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;
    }
  }

  private void applyValidationErrors(CustomEntityValidationExeption e) {
    e.getErrors().forEach((field, messages) -> {
      String text = messages.stream()
          .map(LocalizationManager::getStringByKey)
          .collect(Collectors.joining("\n"));

      switch (field) {
        case "name" -> nameError.set(text);
        case "latitude", "longitude" -> coordinatesError.set(text);
        case "managerId" -> managerError.set(text);
      }
    });
  }

  private void createStation() {
    Station station = new Station(
        name.get(),
        latitude.get(),
        longitude.get(),
        selectedManager.get() == null ? null : selectedManager.get().getId()
    );

    stationService.add(station);
  }

  private void updateStation() {
    String nameValue = isBlank(name.get()) ? editingStation.getName() : name.get().trim();

    Station validated = new Station(
        nameValue,
        latitude.get(),
        longitude.get(),
        selectedManager.get() == null ? null : selectedManager.get().getId()
    );

    editingStation.setName(validated.getName());
    editingStation.setLatitude(String.valueOf(validated.getLatitude()));
    editingStation.setLongitude(String.valueOf(validated.getLongitude()));
    editingStation.setManagerId(validated.getManagerId());

    if (!editingStation.isValid()) {
      throw new CustomEntityValidationExeption(editingStation.getErrors());
    }

    stationService.update(editingStation);

  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  public void setCoordinates(double lat, double lng) {
    latitude.set(String.valueOf(lat));
    longitude.set(String.valueOf(lng));
    locationInfo.set(
        LocalizationManager.getStringByKey("station.location.selected")
            + ": " + String.format("%.5f, %.5f", lat, lng)
    );
    coordinatesError.set("");
  }

  private void clearErrors() {
    nameError.set("");
    coordinatesError.set("");
    managerError.set("");
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

  public StringProperty pickOnMapButtonTextProperty() {
    return pickOnMapButtonText;
  }

  public StringProperty nameLabelTextProperty() {
    return nameLabelText;
  }

  public StringProperty nameProperty() {
    return name;
  }

  public StringProperty locationInfoProperty() {
    return locationInfo;
  }

  public StringProperty nameErrorProperty() {
    return nameError;
  }

  public StringProperty coordinatesErrorProperty() {
    return coordinatesError;
  }
  public StringProperty managerErrorProperty() {return managerError;}
  public StringProperty managerLabelTextProperty() {return  managerLabelText;}
  public ObjectProperty<User> managerSelectedProperty() {return selectedManager;}
  public ObjectProperty<ObservableList<User>> managersProperty() {
    return managers;
  }
}