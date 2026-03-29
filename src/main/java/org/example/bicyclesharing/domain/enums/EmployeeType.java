package org.example.bicyclesharing.domain.enums;

public enum EmployeeType {
  MANAGER("employee.type.MANAGER"),
  TECHNICIAN("employee.type.TECHNICIAN"),
  MECHANIC("employee.type.MECHANIC");

  private final String key;

  EmployeeType(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}