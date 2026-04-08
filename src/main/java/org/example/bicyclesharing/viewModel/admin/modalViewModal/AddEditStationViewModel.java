package org.example.bicyclesharing.viewModel.admin.modalViewModal;

import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditStationViewModel {

  private final StationService stationService;
  private final Station editingStation;

  public final StringProperty titleText = new SimpleStringProperty();
  public final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  public final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");
  public final StringProperty pickOnMapButtonText =
      LocalizationManager.getStringProperty("station.pick.on.map");

  public final StringProperty nameLabelText =
      LocalizationManager.getStringProperty("admin.stations.name");
  public final StringProperty employeeLabelText =
      LocalizationManager.getStringProperty("admin.stations.employee");

  public final StringProperty name = new SimpleStringProperty("");
  public final StringProperty latitude = new SimpleStringProperty("");
  public final StringProperty longitude = new SimpleStringProperty("");
  public final StringProperty locationInfo = new SimpleStringProperty("");

  public final StringProperty nameError = new SimpleStringProperty("");
  public final StringProperty latitudeError = new SimpleStringProperty("");
  public final StringProperty employeeError = new SimpleStringProperty("");


  public AddEditStationViewModel(
      StationService stationService,
      Station editingStation
  ) {
    this.stationService = stationService;
    this.editingStation = editingStation;


    if (editingStation == null) {
      titleText.set(LocalizationManager.getStringByKey("admin.stations.add.title"));
    } else {
      titleText.set(LocalizationManager.getStringByKey("admin.stations.edit.title"));

      setCoordinates(editingStation.getLatitude(), editingStation.getLongitude());
    }
  }

  public boolean isEditMode() {
    return editingStation != null;
  }

  public boolean save() {
    clearErrors();
    try {
      if (editingStation == null) {
        Station station = new Station(
            name.get(),
            latitude.get(),
            longitude.get()
        );

        stationService.add(station);

      } else {
        String nameValue = isBlank(name.get()) ? editingStation.getName() : name.get().trim();

        Station validated = new Station(
            nameValue,
            latitude.get(),
            longitude.get()
        );

        editingStation.setName(validated.getName());
        editingStation.setLatitude(String.valueOf(validated.getLatitude()));
        editingStation.setLongitude(String.valueOf(validated.getLongitude()));

        if (!editingStation.isValid()) {
          throw new CustomEntityValidationExeption(editingStation.getErrors());
        }

        stationService.update(editingStation);
      }

      return true;

    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        String text = messages.stream()
            .map(LocalizationManager::getStringByKey)
            .collect(Collectors.joining("\n"));

        switch (field) {
          case "name" -> nameError.set(text);
          case "latitude", "longitude" -> latitudeError.set(text);
        }
      });
      return false;

    } catch (IllegalArgumentException e) {
      return false;
    }
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
    latitudeError.set("");
  }

  private void clearErrors() {
    nameError.set("");
    latitudeError.set("");
    employeeError.set("");
  }
}