package org.example.bicyclesharing.viewModel.admin.modalViewModal;

import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Employee;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.EmployeeService;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditStationViewModel {

  private final StationService stationService;
  private final EmployeeService employeeService;
  private final Station editingStation;

  public final StringProperty titleText = new SimpleStringProperty();
  public final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  public final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  public final StringProperty nameLabelText =
      LocalizationManager.getStringProperty("admin.stations.name");
  public final StringProperty latitudeLabelText =
      LocalizationManager.getStringProperty("admin.stations.latitude");
  public final StringProperty longitudeLabelText =
      LocalizationManager.getStringProperty("admin.stations.longitude");
  public final StringProperty employeeLabelText =
      LocalizationManager.getStringProperty("admin.stations.employee");

  public final StringProperty name = new SimpleStringProperty("");
  public final StringProperty latitude = new SimpleStringProperty("");
  public final StringProperty longitude = new SimpleStringProperty("");

  public final StringProperty nameError = new SimpleStringProperty("");
  public final StringProperty latitudeError = new SimpleStringProperty("");
  public final StringProperty longitudeError = new SimpleStringProperty("");
  public final StringProperty employeeError = new SimpleStringProperty("");

  public final ObservableList<Employee> employees = FXCollections.observableArrayList();
  public Employee selectedEmployee;

  public AddEditStationViewModel(
      StationService stationService,
      EmployeeService employeeService,
      Station editingStation
  ) {
    this.stationService = stationService;
    this.employeeService = employeeService;
    this.editingStation = editingStation;

    employees.setAll(employeeService.getAll());

    if (editingStation == null) {
      if (!employees.isEmpty()) {
        selectedEmployee = employees.get(0);
      }
    } else {
      employees.stream()
          .filter(emp -> emp.getId().equals(editingStation.getEmployeeId()))
          .findFirst()
          .ifPresentOrElse(
              emp -> selectedEmployee = emp,
              () -> {
                if (!employees.isEmpty()) {
                  selectedEmployee = employees.get(0);
                }
              }
          );
    }

    if (editingStation == null) {
      titleText.set(LocalizationManager.getStringByKey("admin.stations.add.title"));
    } else {
      titleText.set(LocalizationManager.getStringByKey("admin.stations.edit.title"));

      employees.stream()
          .filter(employee -> employee.getId().equals(editingStation.getEmployeeId()))
          .findFirst()
          .ifPresent(employee -> selectedEmployee = employee);
    }
  }

  public boolean isEditMode() {
    return editingStation != null;
  }

  public boolean save() {
    clearErrors();

    if (selectedEmployee == null) {
      employeeError.set(LocalizationManager.getStringByKey("station.employee.empty"));
      return false;
    }

    try {
      if (editingStation == null) {
        Station station = new Station(
            name.get(),
            parseLatitude(latitude.get()),
            parseLongitude(longitude.get())
        );
        station.setEmployeeId(selectedEmployee.getId());

        stationService.add(station);

        selectedEmployee.setStationId(station.getId());
        employeeService.update(selectedEmployee);
      } else {
        String nameValue = isBlank(name.get()) ? editingStation.getName() : name.get().trim();
        double latitudeValue = isBlank(latitude.get())
            ? editingStation.getLatitude()
            : parseLatitude(latitude.get());
        double longitudeValue = isBlank(longitude.get())
            ? editingStation.getLongitude()
            : parseLongitude(longitude.get());

        Station validated = new Station(nameValue, latitudeValue, longitudeValue);

        editingStation.setName(validated.getName());
        editingStation.setLatitude(validated.getLatitude());
        editingStation.setLongitude(validated.getLongitude());
        editingStation.setEmployeeId(selectedEmployee.getId());

        if (!editingStation.isValid()) {
          throw new CustomEntityValidationExeption(editingStation.getErrors());
        }

        stationService.update(editingStation);

        selectedEmployee.setStationId(editingStation.getId());
        employeeService.update(selectedEmployee);
      }

      return true;
    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        String text = messages.stream()
            .map(LocalizationManager::getStringByKey)
            .collect(Collectors.joining("\n"));

        switch (field) {
          case "name" -> nameError.set(text);
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
      latitudeError.set(LocalizationManager.getStringByKey("station.latitude.invalid"));
      throw new IllegalArgumentException();
    }
  }

  private double parseLongitude(String value) {
    try {
      return Double.parseDouble(value.trim());
    } catch (Exception e) {
      longitudeError.set(LocalizationManager.getStringByKey("station.longitude.invalid"));
      throw new IllegalArgumentException();
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void clearErrors() {
    nameError.set("");
    latitudeError.set("");
    longitudeError.set("");
    employeeError.set("");
  }
}