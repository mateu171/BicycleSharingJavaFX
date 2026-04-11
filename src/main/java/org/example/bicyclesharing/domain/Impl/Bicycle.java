package org.example.bicyclesharing.domain.Impl;

import java.util.UUID;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Bicycle extends BaseEntity {

  private String model;
  private TypeBicycle typeBicycle;
  private StateBicycle state;
  private double pricePerMinute;
  private UUID stationId;
  private String imagePath;

  private Bicycle() {
    super();
  }

  public Bicycle(String model,
      TypeBicycle typeBicycle,
      String pricePerMinute,
      UUID stationId) {
    this();
    setModel(model);
    setTypeBicycle(typeBicycle);
    this.state = StateBicycle.AVAILABLE;
    setPricePerMinute(pricePerMinute);
    setStationId(stationId);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public static Bicycle fromDatabase(UUID id,
      String model,
      TypeBicycle typeBicycle,
      StateBicycle state,
      double pricePerMinute,
      UUID stationId,
      String imagePath) {
    Bicycle bicycle = new Bicycle();
    bicycle.setId(id);
    bicycle.model = model;
    bicycle.typeBicycle = typeBicycle;
    bicycle.state = state;
    bicycle.pricePerMinute = pricePerMinute;
    bicycle.stationId = stationId;
    bicycle.imagePath = imagePath;
    return bicycle;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    cleanErrors("model");
    if (model == null || model.trim().isEmpty()) {
      addError("model", "bicycle.model.empty");
    } else if (model.trim().length() < 4 || model.trim().length() > 50) {
      addError("model", "bicycle.model.length");
    }
    this.model = model;
  }

  public TypeBicycle getTypeBicycle() {
    return typeBicycle;
  }

  public void setTypeBicycle(TypeBicycle typeBicycle) {
    cleanErrors("typeBicycle");
    if (typeBicycle == null) {
      addError("typeBicycle", "bicycle.type.empty");
    }
    this.typeBicycle = typeBicycle;
  }

  public StateBicycle getState() {
    return state;
  }

  public void setState(StateBicycle state) {
    this.state = state;
  }

  public double getPricePerMinute() {
    return pricePerMinute;
  }

  public void setPricePerMinute(String priceStr) {
    cleanErrors("pricePerMinute");

    if (priceStr == null || priceStr.trim().isEmpty()) {
      addError("pricePerMinute", "bicycle.price.empty");
      return;
    }

    try {
      double price = Double.parseDouble(priceStr);
      if (price < 0) {
        addError("pricePerMinute", "bicycle.price.negative");
      } else {
        this.pricePerMinute = price;
      }
    } catch (NumberFormatException e) {
      addError("pricePerMinute", "bicycle.price.invalid");
    }
  }

  public UUID getStationId() {
    return stationId;
  }

  public void setStationId(UUID stationId) {
    this.stationId = stationId;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    cleanErrors("imagePath");

    String value = imagePath == null ? null : imagePath.trim();

    if (value != null && value.length() > 255) {
      addError("imagePath", "error.image.path.length");
    }

    this.imagePath = (value == null || value.isEmpty()) ? null : value;
  }
}
