package org.example.bicyclesharing.domain.enums;

public enum StateBicycle {
  AVAILABLE("вільний"), RENTED("в оренді");
  private final String name;

  StateBicycle(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
