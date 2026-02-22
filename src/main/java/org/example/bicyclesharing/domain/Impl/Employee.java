package org.example.bicyclesharing.domain.Impl;

import java.util.UUID;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Employee extends BaseEntity {

  private String name;
  private String phoneNumber;
  private UUID stationId;

  private Employee() {
    super();
  }

  public Employee(String name, String phoneNumber, UUID stationId) {
    this();
    setName(name);
    setPhoneNumber(phoneNumber);
    setStationId(stationId);

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
      addError("name", "Ім’я не може бути пустим!");
    } else if (name.length() < 2 || name.length() > 50) {
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

  @Override
  public String toString() {
    return String.format(
        "Працівник: %s | Телефон: %s | Станція за яку відповідяє: %s",
        name,
        phoneNumber,
        stationId
    );
  }

}
