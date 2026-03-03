package org.example.bicyclesharing.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Transaction;

import java.util.UUID;
import org.example.bicyclesharing.domain.enums.TransactionType;

public class TransactionViewModel {

  private final ObservableList<Transaction> transactions =
      FXCollections.observableArrayList();

  public TransactionViewModel() {

    transactions.add(new Transaction(
        UUID.randomUUID(),
        -19.99,
        TransactionType.TOP_UP,
        "Ride: 2641200"
    ));

    transactions.add(new Transaction(
        UUID.randomUUID(),
        12.41,
        TransactionType.RENTAL_FEE,
        "Promocode bonus"
    ));
  }

  public ObservableList<Transaction> getTransactions() {
    return transactions;
  }
}