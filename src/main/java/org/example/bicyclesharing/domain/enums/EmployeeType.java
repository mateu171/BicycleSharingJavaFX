package org.example.bicyclesharing.domain.enums;

public enum EmployeeType {
  MANAGER("employee.type.MANAGER"),
  TECHNICIAN("employee.type.TECHNICIAN"),
  MECHANIC("employee.type.MECHANIC"),
  OPERATOR("employee.type.OPERATOR");

  private final String name;

  EmployeeType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}