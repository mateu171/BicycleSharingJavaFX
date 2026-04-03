package org.example.bicyclesharing.domain.Impl;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.bicyclesharing.domain.enums.DocumentType;
import org.example.bicyclesharing.domain.enums.ReservationStatus;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Reservation extends BaseEntity {

  private UUID customerId;
  private UUID bicycleId;
  private UUID managerId;

  private LocalDateTime startTime;
  private LocalDateTime endTime;

  private DocumentType documentType;
  private String documentNumber;

  private double depositAmount;
  private boolean depositPaid;
  private boolean inventoryIssued;

  private ReservationStatus status;

  private Reservation() {
    super();
  }

  public Reservation(UUID customerId,
      UUID bicycleId,
      UUID managerId,
      LocalDateTime startTime,
      LocalDateTime endTime,
      DocumentType documentType,
      String documentNumber,
      String depositAmount) {
    this();
    setCustomerId(customerId);
    setBicycleId(bicycleId);
    setManagerId(managerId);
    setStartTime(startTime);
    setEndTime(endTime);
    setDocumentType(documentType);
    setDocumentNumber(documentNumber);
    setDepositAmount(depositAmount);

    this.depositPaid = false;
    this.inventoryIssued = false;
    this.status = ReservationStatus.NEW;

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public static Reservation fromDatabase(UUID id,
      UUID customerId,
      UUID bicycleId,
      UUID managerId,
      LocalDateTime startTime,
      LocalDateTime endTime,
      DocumentType documentType,
      String documentNumber,
      double depositAmount,
      boolean depositPaid,
      boolean inventoryIssued,
      ReservationStatus status) {
    Reservation reservation = new Reservation();
    reservation.setId(id);
    reservation.customerId = customerId;
    reservation.bicycleId = bicycleId;
    reservation.managerId = managerId;
    reservation.startTime = startTime;
    reservation.endTime = endTime;
    reservation.documentType = documentType;
    reservation.documentNumber = documentNumber;
    reservation.depositAmount = depositAmount;
    reservation.depositPaid = depositPaid;
    reservation.inventoryIssued = inventoryIssued;
    reservation.status = status;
    return reservation;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    cleanErrors("customerId");
    if (customerId == null) {
      addError("customerId", "reservation.customer.empty");
    }
    this.customerId = customerId;
  }

  public UUID getBicycleId() {
    return bicycleId;
  }

  public void setBicycleId(UUID bicycleId) {
    cleanErrors("bicycleId");
    if (bicycleId == null) {
      addError("bicycleId", "reservation.bicycle.empty");
    }
    this.bicycleId = bicycleId;
  }

  public UUID getManagerId() {
    return managerId;
  }

  public void setManagerId(UUID managerId) {
    cleanErrors("managerId");
    if (managerId == null) {
      addError("managerId", "reservation.manager.empty");
    }
    this.managerId = managerId;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    cleanErrors("startTime");
    if (startTime == null) {
      addError("startTime", "reservation.start.empty");
    }
    this.startTime = startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    cleanErrors("endTime");

    if (endTime == null) {
      addError("endTime", "reservation.end.empty");
    } else if (startTime != null && endTime.isBefore(startTime)) {
      addError("endTime", "reservation.end.invalid");
    }

    this.endTime = endTime;
  }

  public DocumentType getDocumentType() {
    return documentType;
  }

  public void setDocumentType(DocumentType documentType) {
    cleanErrors("documentType");
    if (documentType == null) {
      addError("documentType", "reservation.documentType.empty");
    }
    this.documentType = documentType;
  }

  public String getDocumentNumber() {
    return documentNumber;
  }

  public void setDocumentNumber(String documentNumber) {
    cleanErrors("documentNumber");

    if (documentNumber == null || documentNumber.trim().isEmpty()) {
      addError("documentNumber", "reservation.documentNumber.empty");
    } else if (!documentNumber.matches("^[A-ZА-ЯІЇЄ0-9]{5,20}$")) {
      addError("documentNumber", "reservation.documentNumber.invalid");
    }

    this.documentNumber = documentNumber;
  }

  public double getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(String depositAmountStr) {
    cleanErrors("depositAmount");

    if (depositAmountStr == null || depositAmountStr.trim().isEmpty()) {
      addError("depositAmount", "reservation.deposit.empty");
      return;
    }

    try {
      double value = Double.parseDouble(depositAmountStr);
      if (value < 0) {
        addError("depositAmount", "reservation.deposit.negative");
      } else {
        this.depositAmount = value;
      }
    } catch (NumberFormatException e) {
      addError("depositAmount", "reservation.deposit.invalid");
    }
  }

  public boolean isDepositPaid() {
    return depositPaid;
  }

  public void setDepositPaid(boolean depositPaid) {
    this.depositPaid = depositPaid;
  }

  public boolean isInventoryIssued() {
    return inventoryIssued;
  }

  public void setInventoryIssued(boolean inventoryIssued) {
    this.inventoryIssued = inventoryIssued;
  }

  public ReservationStatus getStatus() {
    return status;
  }

  public void setStatus(ReservationStatus status) {
    cleanErrors("status");
    if (status == null) {
      addError("status", "reservation.status.empty");
    }
    this.status = status;
  }
}