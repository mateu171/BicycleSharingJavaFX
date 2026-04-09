package org.example.bicyclesharing.domain.enums;

public enum Role {
  ADMIN("role.admin"),
  MECHANIC("role.mechanic"),
  MANAGER("role.manager");

  private final String key;

  Role(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}