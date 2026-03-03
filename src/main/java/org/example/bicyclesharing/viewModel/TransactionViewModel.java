package org.example.bicyclesharing.viewModel;

import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Transaction;

import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.TransactionType;
import org.example.bicyclesharing.services.TransactionService;

public class TransactionViewModel {

  private final ObservableList<Transaction> transactions =
      FXCollections.observableArrayList();
  private final TransactionService transactionService;
  private final User currentUser;

  public TransactionViewModel(TransactionService transactionService, User currentUser) {
    this.transactionService = transactionService;
    this.currentUser = currentUser;

    load();

  }

  public ObservableList<Transaction> getTransactions() {
    return transactions;
  }

  private void load()
  {
    transactions.setAll(transactionService.getByUserId(currentUser.getId()));
  }
}