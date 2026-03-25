package org.example.bicyclesharing.viewModel.user;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Transaction;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.TransactionService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class TransactionViewModel extends BaseViewModel {

  public final StringProperty titleText = LocalizationManager.getStringProperty("transactions.title");

  private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
  private final TransactionService transactionService;

  public TransactionViewModel(TransactionService transactionService, User currentUser) {
    super(currentUser);
    this.transactionService = transactionService;

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