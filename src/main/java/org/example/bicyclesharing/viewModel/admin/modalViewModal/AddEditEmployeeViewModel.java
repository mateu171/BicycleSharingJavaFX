package org.example.bicyclesharing.viewModel.admin.modalViewModal;

import java.util.UUID;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Employee;
import org.example.bicyclesharing.domain.enums.EmployeeType;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.EmployeeService;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditEmployeeViewModel {

  private final EmployeeService employeeService;
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
  public final StringProperty typeLabelText =
      LocalizationManager.getStringProperty("admin.employees.type");
  public final StringProperty salaryLabelText =
      LocalizationManager.getStringProperty("admin.employees.salary");

  public final StringProperty name = new SimpleStringProperty("");
  public final StringProperty phone = new SimpleStringProperty("");
  public final StringProperty stationId = new SimpleStringProperty("");
  public final StringProperty salary = new SimpleStringProperty("");

  public EmployeeType selectedType;

  public final StringProperty nameError = new SimpleStringProperty("");
  public final StringProperty phoneError = new SimpleStringProperty("");
  public final StringProperty stationError = new SimpleStringProperty("");
  public final StringProperty typeError = new SimpleStringProperty("");
  public final StringProperty salaryError = new SimpleStringProperty("");

  public AddEditEmployeeViewModel(EmployeeService employeeService, Employee editingEmployee) {
    this.employeeService = employeeService;
    this.editingEmployee = editingEmployee;

    if (editingEmployee == null) {
      titleText.set(LocalizationManager.getStringByKey("admin.employees.add.title"));
      selectedType = EmployeeType.MANAGER;
    } else {
      titleText.set(LocalizationManager.getStringByKey("admin.employees.edit.title"));
      selectedType = editingEmployee.getType();
    }
  }

  public boolean isEditMode() {
    return editingEmployee != null;
  }

  public boolean save() {
    clearErrors();

    try {
      if (editingEmployee == null) {
        UUID parsedStationId = parseStationId(stationId.get());

        Employee employee = new Employee(
            name.get(),
            phone.get(),
            parsedStationId,
            selectedType,
            salary.get()
        );

        employeeService.add(employee);
      } else {
        String nameValue = isBlank(name.get()) ? editingEmployee.getName() : name.get().trim();
        String phoneValue = isBlank(phone.get()) ? editingEmployee.getPhoneNumber() : phone.get().trim();
        UUID stationValue = isBlank(stationId.get())
            ? editingEmployee.getStationId()
            : parseStationId(stationId.get());
        EmployeeType typeValue = selectedType == null ? editingEmployee.getType() : selectedType;
        String salaryValue = isBlank(salary.get())
            ? String.valueOf(editingEmployee.getSalary())
            : salary.get().trim();

        Employee validated = new Employee(nameValue, phoneValue, stationValue, typeValue, salaryValue);

        editingEmployee.setName(validated.getName());
        editingEmployee.setPhoneNumber(validated.getPhoneNumber());
        editingEmployee.setStationId(validated.getStationId());
        editingEmployee.setType(validated.getType());
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
          case "type" -> typeError.set(text);
          case "salary" -> salaryError.set(text);
        }
      });
      return false;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private UUID parseStationId(String value) {
    if (value == null || value.trim().isEmpty()) {
      stationError.set(LocalizationManager.getStringByKey("employee.station.empty"));
      throw new IllegalArgumentException();
    }

    try {
      return UUID.fromString(value.trim());
    } catch (Exception e) {
      stationError.set(LocalizationManager.getStringByKey("employee.station.invalid"));
      throw new IllegalArgumentException();
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void clearErrors() {
    nameError.set("");
    phoneError.set("");
    stationError.set("");
    typeError.set("");
    salaryError.set("");
  }
}