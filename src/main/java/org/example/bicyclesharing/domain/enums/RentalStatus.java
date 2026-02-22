package org.example.bicyclesharing.domain.enums;

public enum RentalStatus {
  ACTIVE("активний"), INACTIVE("неактивний");

  private final String name;

  RentalStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
