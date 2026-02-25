package org.example.bicyclesharing.exception;

import java.util.List;
import java.util.Map;

public class CustomEntityValidationExeption extends RuntimeException {

  private final Map<String, List<String>> errors;

  public CustomEntityValidationExeption(Map<String, List<String>> errors) {
    super("Помилка валідації. Перевірте 'помилки' для деталей");
    this.errors = errors;
  }

  public Map<String, List<String>> getErrors() {
    return errors;
  }
}
