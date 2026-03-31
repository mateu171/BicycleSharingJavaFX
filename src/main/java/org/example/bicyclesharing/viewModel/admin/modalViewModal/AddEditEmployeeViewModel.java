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

public class AddEditEmployeeViewModel {

  private final EmployeeService employeeService;
  private final StationService stationService;
  private final Employee editingEmployee;

  public final StringProperty titleText = new SimpleStringProperty();
  public final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  public final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  public final StringProperty nameLabelText =
      LocalizationManager.getStringProperty("admin.employees.name");
  public final StringProperty phoneLabelText =
      LocalizationManager.getStringProperty("admin.employees.phone");
  public final StringProperty stationLabelText =
      LocalizationManager.getStringProperty("admin.employees.station");
  public final StringProperty salaryLabelText =
      LocalizationManager.getStringProperty("admin.employees.salary");

  public final StringProperty name = new SimpleStringProperty("");
  public final StringProperty phone = new SimpleStringProperty("");
  public final StringProperty salary = new SimpleStringProperty("");

  public final StringProperty nameError = new SimpleStringProperty("");
  public final StringProperty phoneError = new SimpleStringProperty("");
  public final StringProperty stationError = new SimpleStringProperty("");
  public final StringProperty salaryError = new SimpleStringProperty("");

  public final ObservableList<Station> stations = FXCollections.observableArrayList();

  public Station selectedStation;

  public AddEditEmployeeViewModel(
      EmployeeService employeeService,
      StationService stationService,
      Employee editingEmployee
  ) {
    this.employeeService = employeeService;
    this.stationService = stationService;
    this.editingEmployee = editingEmployee;

    stations.setAll(stationService.getAll());

    if (!stations.isEmpty()) {
      selectedStation = stations.get(0);
    }

    if (editingEmployee == null) {
      titleText.set(LocalizationManager.getStringByKey("admin.employees.add.title"));
    } else {
      titleText.set(LocalizationManager.getStringByKey("admin.employees.edit.title"));

      stations.stream()
          .filter(station -> station.getId().equals(editingEmployee.getStationId()))
          .findFirst()
          .ifPresent(station -> selectedStation = station);
    }
  }

  public boolean isEditMode() {
    return editingEmployee != null;
  }

  public boolean save() {
    clearErrors();

    if (selectedStation == null) {
      stationError.set(LocalizationManager.getStringByKey("employee.station.empty"));
      return false;
    }

    try {
      if (editingEmployee == null) {
        Employee employee = new Employee(
            name.get(),
            phone.get(),
            selectedStation.getId(),
            salary.get()
        );

        employeeService.add(employee);
      } else {
        String nameValue = isBlank(name.get())
            ? editingEmployee.getName()
            : name.get().trim();

        String phoneValue = isBlank(phone.get())
            ? editingEmployee.getPhoneNumber()
            : phone.get().trim();

        String salaryValue = isBlank(salary.get())
            ? String.valueOf(editingEmployee.getSalary())
            : salary.get().trim();

        var stationValue = selectedStation == null
            ? editingEmployee.getStationId()
            : selectedStation.getId();

        Employee validated = new Employee(
            nameValue,
            phoneValue,
            stationValue,
            salaryValue
        );

        editingEmployee.setName(validated.getName());
        editingEmployee.setPhoneNumber(validated.getPhoneNumber());
        editingEmployee.setStationId(validated.getStationId());
        editingEmployee.setSalary(validated.getSalary());

        if (!editingEmployee.isValid()) {
          throw new CustomEntityValidationExeption(editingEmployee.getErrors());
        }

        employeeService.update(editingEmployee);
      }

      return true;
    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        String text = messages.stream()
            .map(LocalizationManager::getStringByKey)
            .collect(Collectors.joining("\n"));

        switch (field) {
          case "name" -> nameError.set(text);
          case "phoneNumber" -> phoneError.set(text);
          case "stationId" -> stationError.set(text);
          case "salary" -> salaryError.set(text);
        }
      });
      return false;
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void clearErrors() {
    nameError.set("");
    phoneError.set("");
    stationError.set("");
    salaryError.set("");
  }
}