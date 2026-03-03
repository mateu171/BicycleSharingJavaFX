package org.example.bicyclesharing.domain.Impl;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.bicyclesharing.domain.enums.TransactionType;

public class Transaction extends BaseEntity {

  private UUID userId;
  private double amount;
  private TransactionType type;
  private LocalDateTime timestamp;
  private String description;

  public Transaction(UUID userId, double amount, TransactionType type, String description) {
    this.userId = userId;
    this.amount = amount;
    this.type = type;
    this.timestamp = LocalDateTime.now();
    this.description = description;
  }

  public Transaction(UUID userId,
      double amount,
      TransactionType type,
      LocalDateTime timestamp,
      String description) {

    this.userId = userId;
    this.amount = amount;
    this.type = type;
    this.timestamp = timestamp;
    this.description = description;
  }
  public UUID getUserId() { return userId; }
  public double getAmount() { return amount; }
  public TransactionType getType() { return type; }
  public LocalDateTime getTimestamp() { return timestamp; }
  public String getDescription() { return description; }
}