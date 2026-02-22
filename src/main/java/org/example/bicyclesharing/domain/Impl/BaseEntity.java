package org.example.bicyclesharing.domain.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.interfaces.Entity;

public abstract class BaseEntity implements Entity {

  private final UUID id;
  protected HashMap<String, List<String>> errors;

  protected BaseEntity() {
    this.id = UUID.randomUUID();
    this.errors = new HashMap<>();
  }

  protected void addError(String field, String message) {
    this.errors
        .computeIfAbsent(field, k -> new ArrayList<>()).add(message);

  }

  protected void cleanErrors(String field) {
    this.errors.remove(field);
  }

  public HashMap<String, List<String>> getErrors() {
    return new HashMap<>(errors);
  }

  public boolean isValid() {
    return errors.isEmpty();
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    BaseEntity that = (BaseEntity) obj;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
