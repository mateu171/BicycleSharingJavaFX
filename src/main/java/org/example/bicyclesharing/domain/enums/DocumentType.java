package org.example.bicyclesharing.domain.enums;

public enum DocumentType {
  PASSPORT("document.passport"),
  ID_CARD("document.id_card"),
  DRIVER_LICENSE("document.driver_license");

  private final String key;

  DocumentType(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}