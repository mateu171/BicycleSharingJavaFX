package org.example.bicyclesharing.domain.enums;

public enum ReservationStatus {
  NEW("reservation.status.new"),
  CONFIRMED("reservation.status.confirmed"),
  ISSUED("reservation.status.issued"),
  COMPLETED("reservation.status.completed"),
  CANCELLED("reservation.status.cancelled");

  private final String key;

  ReservationStatus(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}