package org.example.bicyclesharing.viewModel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.UserService;

public class BalanceViewModel {

  private final DoubleProperty balance = new SimpleDoubleProperty(0);
  private final UserService userService;
  private final User currentUser;

  public BalanceViewModel(UserService userService, User currentUser) {
    this.userService = userService;
    this.currentUser = currentUser;
    this.balance.set(currentUser.getBalance());
  }

  public DoubleProperty balanceProperty() {
    return balance;
  }

  public double getBalance() {
    return balance.get();
  }

  public void setBalance(double value) {
    balance.set(value);
  }

  // Метод для додавання балансу та збереження в БД
  public void addBalance(double amount) {
    balance.set(balance.get() + amount);
    currentUser.setBalance(balance.get());
    userService.update(currentUser); // зберігаємо новий баланс у БД
  }
}