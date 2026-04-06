package org.example.bicyclesharing.domain.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Station extends BaseEntity {

  private String name;
  private double latitude;
  private double longitude;
  private List<UUID> bicyclesId;
  private UUID employeeId;

  private Station() {
    super();
    this.bicyclesId = new ArrayList<>();
  }

  public Station(String name, String latitude, String longitude) {
    this();
    setName(name);
    setLatitude(latitude);
    setLongitude(longitude);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public static Station fromDatabase(UUID id,
      String name,
      double latitude,
      double longitude,
      List<UUID> bicyclesId,
      UUID employeeId) {
    Station station = new Station();
    station.setId(id);
    station.name = name;
    station.latitude = latitude;
    station.longitude = longitude;
    station.bicyclesId = bicyclesId != null ? bicyclesId : new ArrayList<>();
    station.employeeId = employeeId;
    return station;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    cleanErrors("name");
    if (name == null || name.trim().isEmpty()) {
      addError("name", "station.name.empty");
    } else if (name.trim().length() < 2 || name.trim().length() > 50) {
      addError("name", "station.name.length");
    }
    this.name = name;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(String value) {
    cleanErrors("latitude");

    if (value == null || value.trim().isEmpty()) {
      addError("latitude", "station.latitude.empty");
      return;
    }

    try {
      double lat = Double.parseDouble(value.trim());

      if (lat < -90 || lat > 90) {
        addError("latitude", "station.latitude.range");
      } else {
        this.latitude = lat;
      }

    } catch (NumberFormatException e) {
      addError("latitude", "station.latitude.invalid");
    }
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(String value) {
    cleanErrors("longitude");

    if (value == null || value.trim().isEmpty()) {
      addError("longitude", "station.longitude.empty");
      return;
    }

    try {
      double lng = Double.parseDouble(value.trim());

      if (lng < -180 || lng > 180) {
        addError("longitude", "station.longitude.range");
      } else {
        this.longitude = lng;
      }

    } catch (NumberFormatException e) {
      addError("longitude", "station.longitude.invalid");
    }
  }

  public List<UUID> getBicyclesId() {
    return new ArrayList<>(bicyclesId);
  }

  public void addBicycleId(UUID bicycleId) {
    if (bicycleId != null && !bicyclesId.contains(bicycleId)) {
      bicyclesId.add(bicycleId);
    }
  }

  public void removeBicycleId(UUID bicycleId) {
    bicyclesId.remove(bicycleId);
  }

  public UUID getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(UUID employeeId) {
    this.employeeId = employeeId;
  }
}