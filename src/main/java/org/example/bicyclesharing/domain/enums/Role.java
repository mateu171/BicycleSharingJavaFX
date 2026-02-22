package org.example.bicyclesharing.domain.enums;

public enum Role {
  CLIENT("користувач"), ADMIN("адміністратор");
  private final Object name;

  Role(String name) {
    this.name = name;
  }

  public Object getName() {
    return name;
  }
}
