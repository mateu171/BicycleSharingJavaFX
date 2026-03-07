package org.example.bicyclesharing.viewModel;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Transaction;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.TransactionService;
import org.example.bicyclesharing.util.LocalizationManager;

public class TransactionViewModel {

  public final StringProperty titleText = LocalizationManager.getStringProperty("transactions.title");

  private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
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