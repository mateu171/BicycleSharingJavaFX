package org.example.bicyclesharing.domain.Impl;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.bicyclesharing.domain.enums.MaintenanceAction;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class MaintenanceRecord extends BaseEntity {

  private UUID bicycleId;
  private UUID mechanicId;
  private MaintenanceType type;
  private String description;
  private String result;
  private MaintenanceAction action;
  private LocalDateTime createdAt;

  private MaintenanceRecord() {
    super();
  }

  public MaintenanceRecord(UUID bicycleId,
      UUID mechanicId,
      MaintenanceType type,
      String description,
      String result,
      MaintenanceAction action) {
    this();
    setBicycleId(bicycleId);
    setMechanicId(mechanicId);
    setType(type);
    setDescription(description);
    setResult(result);
    setAction(action);
    setCreatedAt(LocalDateTime.now());

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public static MaintenanceRecord fromDatabase(UUID id,
      UUID bicycleId,
      UUID mechanicId,
      MaintenanceType type,
      String description,
      String result,
      MaintenanceAction action,
      LocalDateTime createdAt) {
    MaintenanceRecord record = new MaintenanceRecord();
    record.setId(id);
    record.bicycleId = bicycleId;
    record.mechanicId = mechanicId;
    record.type = type;
    record.description = description;
    record.result = result;
    record.action = action;
    record.createdAt = createdAt;
    return record;
  }

  public UUID getBicycleId() {
    return bicycleId;
  }

  public void setBicycleId(UUID bicycleId) {
    cleanErrors("bicycleId");
    if (bicycleId == null) {
      addError("bicycleId", "maintenance.bicycle.empty");
    }
    this.bicycleId = bicycleId;
  }

  public UUID getMechanicId() {
    return mechanicId;
  }

  public void setMechanicId(UUID mechanicId) {
    cleanErrors("mechanicId");
    if (mechanicId == null) {
      addError("mechanicId", "maintenance.mechanic.empty");
    }
    this.mechanicId = mechanicId;
  }

  public MaintenanceType getType() {
    return type;
  }

  public void setType(MaintenanceType type) {
    cleanErrors("type");
    if (type == null) {
      addError("type", "maintenance.type.empty");
    }
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    cleanErrors("description");

    if (description == null || description.trim().isEmpty()) {
      addError("description", "maintenance.description.empty");
    } else if (description.trim().length() < 5 || description.trim().length() > 500) {
      addError("description", "maintenance.description.length");
    }

    this.description = description;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    cleanErrors("result");

    if (result == null || result.trim().isEmpty()) {
      addError("result", "maintenance.result.empty");
    } else if (result.trim().length() < 3 || result.trim().length() > 500) {
      addError("result", "maintenance.result.length");
    }

    this.result = result;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    cleanErrors("createdAt");
    if (createdAt == null) {
      addError("createdAt", "maintenance.createdAt.empty");
    }
    this.createdAt = createdAt;
  }

  public void setAction(MaintenanceAction action) {
    cleanErrors("action");

    if (action == null) {
      addError("action", "maintenance.action.empty");
    }

    this.action = action;
  }

  public MaintenanceAction getAction() {
    return action;
  }
}