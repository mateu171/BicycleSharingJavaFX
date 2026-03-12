package org.example.bicyclesharing.domain.Impl;

import java.util.UUID;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Bicycle extends BaseEntity {

  private String model;
  private TypeBicycle typeBicycle;
  private StateBicycle state;
  private double pricePerHour;
  private UUID rentalId;
  private double latitude;
  private double longitude;

  private Bicycle() {
    super();
  }

  public Bicycle(String model, String pricePerHour,
      double latitude, double longitude) {
    this();
    setModel(model);
    this.typeBicycle = TypeBicycle.URBAN;
    this.state = StateBicycle.AVAILABLE;
    setPricePerHour(pricePerHour);

    setLatitude(latitude);
    setLongitude(longitude);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public UUID getRentalId() {
    return rentalId;
  }

  public void setRentalId(UUID rentalId) {
    cleanErrors("rentalId");
    if (rentalId == null) {
      addError("rentalId", "Оренда не може бути пустою");
    }
    this.rentalId = rentalId;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    cleanErrors("model");
    if (model == null || model.trim().isEmpty()) {
      addError("model", "Моделя для велосипеда не повинна бути пустою");
    } else if (model.length() < 4 || model.length() > 15) {
      addError("model", "Модель велосипеда повинна бути не менше 4 та не більше 15 символів");
    }
    this.model = model;
  }

  public TypeBicycle getTypeBicycle() {
    return typeBicycle;
  }

  public StateBicycle getState() {
    return state;
  }

  public void setState(StateBicycle state) {
    this.state = state;
  }

  public double getPricePerHour() {
    return pricePerHour;
  }

  public void setPricePerHour(String priceStr) {
    cleanErrors("pricePerHour");

    if (priceStr == null || priceStr.trim().isEmpty()) {
      addError("pricePerHour", "Ціна за годину не може бути пустою!");
      return;
    }

    try {
      double price = Double.parseDouble(priceStr);
      if (price < 0) {
        addError("pricePerHour", "Ціна за годину не може бути менше 0!");
      } else {
        this.pricePerHour = price;
      }
    } catch (NumberFormatException e) {
      addError("pricePerHour", "Ціна за годину повинна бути числом!");
    }
  }
  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    cleanErrors("latitude");

    if (latitude < -90 || latitude > 90) {
      addError("latitude", "Широта повинна бути від -90 до 90");
    }

    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    cleanErrors("longitude");

    if (longitude < -180 || longitude > 180) {
      addError("longitude", "Довгота повинна бути від -180 до 180");
    }

    this.longitude = longitude;
  }


}
