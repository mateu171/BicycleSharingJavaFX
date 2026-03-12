package org.example.bicyclesharing.domain.enums;

public enum TypeBicycle {
   URBAN("міський");

  private final String name;

  TypeBicycle(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
