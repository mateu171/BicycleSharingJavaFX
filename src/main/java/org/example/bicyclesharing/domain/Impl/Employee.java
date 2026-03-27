package org.example.bicyclesharing.domain.Impl;

import java.util.UUID;
import org.example.bicyclesharing.domain.enums.EmployeeType;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Employee extends BaseEntity {

  private String name;
  private String phoneNumber;
  private UUID stationId;
  private EmployeeType type;
  private double salary;

  private Employee() {
    super();
  }

  public Employee(String name, String phoneNumber, UUID stationId, EmployeeType type, String salary) {
    this();
    setName(name);
    setPhoneNumber(phoneNumber);
    setStationId(stationId);
    setType(type);
    setSalary(salary);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public static Employee fromDatabase(UUID id, String name, String phoneNumber, UUID stationId,
      EmployeeType type, double salary) {
    Employee employee = new Employee();
    employee.setId(id);
    employee.name = name;
    employee.phoneNumber = phoneNumber;
    employee.stationId = stationId;
    employee.type = type;
    employee.salary = salary;
    return employee;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    cleanErrors("name");
    if (name == null || name.trim().isEmpty()) {
      addError("name", "Ім’я не може бути пустим!");
    } else if (name.trim().length() < 2 || name.trim().length() > 50) {
      addError("name", "Ім’я повинно бути від 2 до 50 символів!");
    }
    this.name = name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    cleanErrors("phoneNumber");
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      addError("phoneNumber", "Номер телефону не може бути пустим!");
    } else if (!phoneNumber.matches("^\\+?[0-9]{7,15}$")) {
      addError("phoneNumber",
          "Невірний формат телефону! Можна вводити лише цифри та опційно + спереду.");
    }
    this.phoneNumber = phoneNumber;
  }

  public UUID getStationId() {
    return stationId;
  }

  public void setStationId(UUID stationId) {
    cleanErrors("stationId");
    if (stationId == null) {
      addError("stationId", "Станція не може бути null!");
    }
    this.stationId = stationId;
  }

  public EmployeeType getType() {
    return type;
  }

  public void setType(EmployeeType type) {
    cleanErrors("type");
    if (type == null) {
      addError("type", "Тип працівника не може бути пустим!");
    }
    this.type = type;
  }

  public double getSalary() {
    return salary;
  }

  public void setSalary(String salary) {
    cleanErrors("salary");

    if (salary == null || salary.trim().isEmpty()) {
      addError("salary", "Зарплата не може бути пустою!");
      return;
    }

    try {
      double parsedSalary = Double.parseDouble(salary);
      if (parsedSalary < 0) {
        addError("salary", "Зарплата не може бути від’ємною!");
      } else {
        this.salary = parsedSalary;
      }
    } catch (NumberFormatException e) {
      addError("salary", "Невірний формат зарплати!");
    }
  }

  public void setSalary(double salary) {
    cleanErrors("salary");
    if (salary < 0) {
      addError("salary", "Зарплата не може бути від’ємною!");
    } else {
      this.salary = salary;
    }
  }

  @Override
  public String toString() {
    return String.format(
        "Працівник: %s | Телефон: %s | Тип: %s | Зарплата: %.2f | Станція за яку відповідає: %s",
        name,
        phoneNumber,
        type != null ? type.getName() : "—",
        salary,
        stationId
    );
  }
}