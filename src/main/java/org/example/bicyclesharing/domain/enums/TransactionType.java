package org.example.bicyclesharing.domain.enums;

import org.example.bicyclesharing.util.LocalizationManager;

public enum TransactionType {
  TOP_UP,
  RENTAL_FEE;

  public String getLocalizedName() {
    switch (this) {
      case TOP_UP -> { return LocalizationManager.getStringByKey("transaction.top_up"); }
      case RENTAL_FEE -> { return LocalizationManager.getStringByKey("transaction.rental_fee"); }
      default -> throw new IllegalArgumentException();
    }
  }
}
