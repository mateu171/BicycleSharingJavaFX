package org.example.bicyclesharing.domain.Impl;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Rental extends BaseEntity {

  private UUID customerId;
  private UUID bicycleId;
  private LocalDateTime start;
  private LocalDateTime end;
  private double totalCost;

  public Rental() {
    super();
  }

  public Rental(UUID customerId, UUID bicycleId) {
    this();
    setCustomerId(customerId);
    setBicycleId(bicycleId);
    setStart(LocalDateTime.now());

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    cleanErrors("userId");
    if (customerId == null) {
      addError("userId", "Користувач не може бути null!");
    }
    this.customerId = customerId;
  }

  public UUID getBicycleId() {
    return bicycleId;
  }

  public void setBicycleId(UUID bicycleId) {
    cleanErrors("bicycleId");
    if (bicycleId == null) {
      addError("bicycleId", "Велосипед не може бути null!");
    }
    this.bicycleId = bicycleId;
  }

  public LocalDateTime getStart() {
    return start;
  }

  public void setStart(LocalDateTime start) {
    cleanErrors("start");
    if (start == null) {
      addError("start", "Дата початку не може бути null!");
    }
    this.start = start;
  }

  public LocalDateTime getEnd() {
    return end;
  }

  public void setEnd(LocalDateTime end) {
    cleanErrors("end");
    if (end != null && start != null && end.isBefore(start)) {
      addError("end", "Дата завершення не може бути раніше дати початку!");
    }
    this.end = end;
  }

  public double getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(double totalCost) {
    this.totalCost = totalCost;
  }

}
