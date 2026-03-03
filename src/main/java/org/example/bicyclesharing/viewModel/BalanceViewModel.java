package org.example.bicyclesharing.viewModel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.example.bicyclesharing.domain.Impl.Transaction;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.TransactionType;
import org.example.bicyclesharing.services.TransactionService;
import org.example.bicyclesharing.services.UserService;

public class BalanceViewModel {

  private final DoubleProperty balance = new SimpleDoubleProperty(0);
  private final UserService userService;
  private final TransactionService transactionService;
  private final User currentUser;

  public BalanceViewModel(UserService userService, TransactionService transactionService, User currentUser) {
    this.userService = userService;
    this.transactionService = transactionService;
    this.currentUser = currentUser;
    this.balance.set(currentUser.getBalance());
  }

  public DoubleProperty balanceProperty() {
    return balance;
  }

  public void addBalance(double amount) {
    balance.set(balance.get() + amount);
    currentUser.setBalance(balance.get());
    Transaction tr = new Transaction(currentUser.getId(),amount, TransactionType.TOP_UP,"Баланс поповнено");
    transactionService.add(tr);
    userService.update(currentUser);
  }
}