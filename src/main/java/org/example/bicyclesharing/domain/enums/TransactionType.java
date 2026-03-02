package org.example.bicyclesharing.domain.enums;

public enum TransactionType {
  TOP_UP("Поповнення балансу"),
  RENTAL_FEE("Оплата оренди");

  private final String name;

  TransactionType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
