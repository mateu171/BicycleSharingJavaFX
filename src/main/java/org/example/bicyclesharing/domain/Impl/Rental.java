package org.example.bicyclesharing.domain.Impl;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.bicyclesharing.domain.enums.RentalStatus;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Rental extends BaseEntity {

  private UUID userId;
  private UUID bicycleId;
  private UUID stationId;
  private RentalStatus rentalStatus;
  private LocalDateTime start;
  private LocalDateTime end;
  private double totalCost;

  private Rental() {
    super();
  }

  public Rental(UUID userId, UUID bicycleId, UUID stationId) {
    this();
    setUserId(userId);
    setBicycleId(bicycleId);
    setStationId(stationId);
    setStart(LocalDateTime.now());
    setRentalStatus(RentalStatus.ACTIVE);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    cleanErrors("userId");
    if (userId == null) {
      addError("userId", "Користувач не може бути null!");
    }
    this.userId = userId;
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

  public UUID getStationId() {
    return stationId;
  }

  public void setStationId(UUID stationId) {
    cleanErrors("startStationId");
    if (stationId == null) {
      addError("startStationId", "Початкова станція не може бути null!");
    }
    this.stationId = stationId;
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

  public RentalStatus getRentalStatus() {
    return rentalStatus;
  }

  public void setRentalStatus(RentalStatus rentalStatus) {
    this.rentalStatus = rentalStatus;
  }

  @Override
  public String toString() {
    return String.format(
        "Оренда | Користувач: %s | Велосипед: %s | Станція: %s | Початок: %s | Кінець: %s | Загальна сума: %.2f грн | Статус: %s",
        userId,
        bicycleId,
        stationId,
        start,
        end,
        totalCost,
        rentalStatus.getName()
    );
  }

}
