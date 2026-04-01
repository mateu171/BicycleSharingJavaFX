package org.example.bicyclesharing.domain.Impl;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class MaintenanceRecord extends BaseEntity {

  private UUID bicycleId;
  private UUID mechanicId;
  private UUID issueId;
  private MaintenanceType type;
  private String description;
  private String result;
  private boolean returnedToAvailable;
  private boolean writtenOff;
  private LocalDateTime createdAt;

  private MaintenanceRecord() {
    super();
  }

  public MaintenanceRecord(UUID bicycleId,
      UUID mechanicId,
      UUID issueId,
      MaintenanceType type,
      String description,
      String result,
      boolean returnedToAvailable,
      boolean writtenOff) {
    this();
    setBicycleId(bicycleId);
    setMechanicId(mechanicId);
    setIssueId(issueId);
    setType(type);
    setDescription(description);
    setResult(result);
    setReturnedToAvailable(returnedToAvailable);
    setWrittenOff(writtenOff);
    setCreatedAt(LocalDateTime.now());

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public static MaintenanceRecord fromDatabase(UUID id,
      UUID bicycleId,
      UUID mechanicId,
      UUID issueId,
      MaintenanceType type,
      String description,
      String result,
      boolean returnedToAvailable,
      boolean writtenOff,
      LocalDateTime createdAt) {
    MaintenanceRecord record = new MaintenanceRecord();
    record.setId(id);
    record.bicycleId = bicycleId;
    record.mechanicId = mechanicId;
    record.issueId = issueId;
    record.type = type;
    record.description = description;
    record.result = result;
    record.returnedToAvailable = returnedToAvailable;
    record.writtenOff = writtenOff;
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

  public UUID getIssueId() {
    return issueId;
  }

  public void setIssueId(UUID issueId) {
    this.issueId = issueId;
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

  public boolean isReturnedToAvailable() {
    return returnedToAvailable;
  }

  public void setReturnedToAvailable(boolean returnedToAvailable) {
    if (returnedToAvailable && writtenOff) {
      cleanErrors("statusFlags");
      addError("statusFlags", "maintenance.flags.conflict");
    } else {
      cleanErrors("statusFlags");
    }
    this.returnedToAvailable = returnedToAvailable;
  }

  public boolean isWrittenOff() {
    return writtenOff;
  }

  public void setWrittenOff(boolean writtenOff) {
    if (writtenOff && returnedToAvailable) {
      cleanErrors("statusFlags");
      addError("statusFlags", "maintenance.flags.conflict");
    } else {
      cleanErrors("statusFlags");
    }
    this.writtenOff = writtenOff;
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
}