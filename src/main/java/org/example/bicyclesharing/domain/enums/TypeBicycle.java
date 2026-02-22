package org.example.bicyclesharing.domain.enums;

public enum TypeBicycle {
  MOUNTAIN("гірський"), HIGHWAY("шосейний"), URBAN("міський");

  private final String name;

  TypeBicycle(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
