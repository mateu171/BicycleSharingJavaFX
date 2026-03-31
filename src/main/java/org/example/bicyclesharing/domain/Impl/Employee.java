package org.example.bicyclesharing.domain.Impl;

import java.util.UUID;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Employee extends BaseEntity {

  private String name;
  private String phoneNumber;
  private UUID stationId;
  private double salary;

  private Employee() {
    super();
  }

  public Employee(String name, String phoneNumber, UUID stationId, String salary) {
    this();
    setName(name);
    setPhoneNumber(phoneNumber);
    setStationId(stationId);
    setSalary(salary);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    cleanErrors("name");
    if (name == null || name.trim().isEmpty()) {
      addError("name", "employee.name.empty");
    } else if (name.trim().length() < 2 || name.trim().length() > 50) {
      addError("name", "employee.name.length");
    }
    this.name = name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    cleanErrors("phoneNumber");
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      addError("phoneNumber", "employee.phone.empty");
    } else if (!phoneNumber.matches("^\\+?[0-9]{7,15}$")) {
      addError("phoneNumber", "employee.phone.invalid");
    }
    this.phoneNumber = phoneNumber;
  }

  public UUID getStationId() {
    return stationId;
  }

  public void setStationId(UUID stationId) {
    cleanErrors("stationId");
    if (stationId == null) {
      addError("stationId", "employee.station.empty");
    }
    this.stationId = stationId;
  }

  public double getSalary() {
    return salary;
  }

  public void setSalary(String salary) {
    cleanErrors("salary");

    if (salary == null || salary.trim().isEmpty()) {
      addError("salary", "employee.salary.empty");
      return;
    }

    try {
      double parsedSalary = Double.parseDouble(salary);
      if (parsedSalary < 0) {
        addError("salary", "employee.salary.negative");
      } else {
        this.salary = parsedSalary;
      }
    } catch (NumberFormatException e) {
      addError("salary", "employee.salary.invalid");
    }
  }

  public void setSalary(double salary) {
    cleanErrors("salary");
    if (salary < 0) {
      addError("salary", "employee.salary.negative");
    } else {
      this.salary = salary;
    }
  }
}