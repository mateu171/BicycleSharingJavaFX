package org.example.bicyclesharing.domain.enums;

public enum StateBicycle {
  AVAILABLE("state.available"),
  RENTED("state.rented");

  private final String key;

  StateBicycle(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
