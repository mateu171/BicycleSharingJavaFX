package org.example.bicyclesharing.domain.enums;

public enum TypeBicycle {
  URBAN("type.urban"),
  MOUNTAIN("type.mountain"),
  ROAD("type.road"),
  ELECTRIC("type.electric");

  private final String key;

  TypeBicycle(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
