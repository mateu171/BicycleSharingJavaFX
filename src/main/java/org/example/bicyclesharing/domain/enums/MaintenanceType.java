package org.example.bicyclesharing.domain.enums;

public enum MaintenanceType {
  PRE_RENT_INSPECTION("maintenance.type.pre_rent_inspection"),
  POST_RENT_INSPECTION("maintenance.type.post_rent_inspection"),
  REPAIR("maintenance.type.repair"),
  SERVICE("maintenance.type.service"),
  WRITE_OFF("maintenance.type.write_off");

  private final String key;

  MaintenanceType(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
