package org.example.bicyclesharing.domain.enums;

public enum Role {
  CLIENT("Користувач"),
  ADMIN("Адміністратор");

  private final String name;

  Role(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
