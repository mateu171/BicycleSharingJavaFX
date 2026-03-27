package org.example.bicyclesharing.domain.enums;

public enum EmployeeType {
  MANAGER("Менеджер"),
  TECHNICIAN("Технік"),
  OPERATOR("Оператор");

  private final String name;

  EmployeeType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}