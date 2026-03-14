package org.example.bicyclesharing.viewModel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Transaction;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.TransactionType;
import org.example.bicyclesharing.services.TransactionService;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.util.LocalizationManager;

public class BalanceViewModel extends  BaseViewModel{
  public final StringProperty titleText = LocalizationManager.getStringProperty("balance.title");
  public final StringProperty yourBalanceText = LocalizationManager.getStringProperty("balance.yourBalance");
  public final StringProperty chooseAmountText = LocalizationManager.getStringProperty("balance.chooseAmount");
  public final StringProperty topUpAmountText = LocalizationManager.getStringProperty("balance.topUpAmount");
  public final StringProperty topUpButtonText = LocalizationManager.getStringProperty("balance.topUp");
  public final StringProperty amount = new SimpleStringProperty("");

  private final DoubleProperty balance = new SimpleDoubleProperty(0);
  private final UserService userService;
  private final TransactionService transactionService;

  public BalanceViewModel(UserService userService, TransactionService transactionService, User currentUser) {
    super(currentUser);
    this.userService = userService;
    this.transactionService = transactionService;
    this.balance.set(currentUser.getBalance());
  }

  public DoubleProperty balanceProperty() {
    return balance;
  }

  public void addBalance() {

    double value;

    try {
      value = Double.parseDouble(amount.get());
    } catch (Exception e) {
      return;
    }

    if (value < 1) return;

    balance.set(balance.get() + value);
    currentUser.setBalance(balance.get());

    Transaction tr = new Transaction(
        currentUser.getId(),
        value,
        TransactionType.TOP_UP,
        "balance.recharged"
    );

    transactionService.add(tr);
    userService.update(currentUser);
  }
}