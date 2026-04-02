package org.example.bicyclesharing.domain.enums;

public enum MaintenanceAction {
  RETURN_TO_AVAILABLE("maintenance.returned"),
  WRITE_OFF("maintenance.write_off");

  private final String key;

  MaintenanceAction(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}