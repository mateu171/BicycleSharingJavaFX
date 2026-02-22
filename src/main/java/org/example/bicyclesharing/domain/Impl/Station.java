package org.example.bicyclesharing.domain.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Station extends BaseEntity {

  private final List<UUID> employeesId;
  private String name;
  private String address;
  private int countPlaces;

  private Station() {
    super();
    this.employeesId = new ArrayList<>();
  }

  public Station(String name, String address, String countPlaces) {
    this();
    setName(name);
    setAddress(address);
    setCountPlaces(countPlaces);

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
      addError("name", "Назва станції не може бути пустою!");
    } else if (name.length() < 2 || name.length() > 50) {
      addError("name", "Назва станції повинна бути від 2 до 50 символів!");
    }
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    cleanErrors("address");
    if (address == null || address.trim().isEmpty()) {
      addError("address", "Адреса не може бути пустою!");
    } else {
      String pattern = "^вул\\.\\s+[А-Яа-яЇїІіЄєҐґ\\s]+,\\s*\\d+,\\s*[А-Яа-яЇїІіЄєҐґ\\s]+$";
      if (!address.matches(pattern)) {
        addError("address", "Адреса повинна бути у форматі: вул. <Назва>, <номер>, <Місто>");
      } else if (address.length() < 10 || address.length() > 100) {
        addError("address", "Адреса повинна бути від 10 до 100 символів!");
      }
    }
    this.address = address;
  }

  public int getCountPlaces() {
    return countPlaces;
  }

  public void setCountPlaces(String countPlacesStr) {
    cleanErrors("countPlaces");

    if (countPlacesStr == null || countPlacesStr.trim().isEmpty()) {
      addError("countPlaces", "Кількість місць не може бути пустою!");
      return;
    }

    try {
      int count = Integer.parseInt(countPlacesStr.trim());
      if (count < 0) {
        addError("countPlaces", "Кількість місць не може бути від’ємною!");
      } else if (count > 500) {
        addError("countPlaces", "Кількість місць не може перевищувати 500!");
      } else {
        this.countPlaces = count;
      }
    } catch (NumberFormatException e) {
      addError("countPlaces", "Кількість місць повинна бути цілим числом!");
    }
  }

  public List<UUID> getEmployeesId() {
    return employeesId;
  }

  public void setEmployeeId(UUID employeeId) {
    cleanErrors("employeesId");
    if (employeeId == null) {
      addError("employeesId", "UUID працівника не може бути null!");
      return;
    }
    if (employeesId.contains(employeeId)) {
      addError("employeesId", "Цей працівник вже доданий до станції!");
      return;
    }
    employeesId.add(employeeId);
  }

  @Override
  public String toString() {
    return String.format(
        "Станція: %s | Адреса: %s | Кількість місць: %d | Працівників: %d",
        name,
        address,
        countPlaces,
        employeesId == null ? 0 : employeesId.size()
    );
  }
}
