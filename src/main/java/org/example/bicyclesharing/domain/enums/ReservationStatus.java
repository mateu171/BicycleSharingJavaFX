package org.example.bicyclesharing.domain.enums;

public enum ReservationStatus {
  NEW("reservation.status.new"),
  ISSUED("reservation.status.issued"),
  CANCELLED("reservation.status.cancelled");

  private final String key;

  ReservationStatus(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}