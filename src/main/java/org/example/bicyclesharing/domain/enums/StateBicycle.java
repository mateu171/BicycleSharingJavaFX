package org.example.bicyclesharing.domain.enums;

public enum StateBicycle {
  AVAILABLE("state.available"),
  RENTED("state.rented"),
  NEEDS_INSPECTION("state.needs.inspection"),
  ON_MAINTENANCE("state.on.maintenance"),
  UNAVAILABLE("state.unavailable");

  private final String key;

  StateBicycle(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
