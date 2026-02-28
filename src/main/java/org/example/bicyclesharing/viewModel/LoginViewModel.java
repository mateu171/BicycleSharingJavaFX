package org.example.bicyclesharing.viewModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.AuthException;
import org.example.bicyclesharing.services.AuthService;
import org.example.bicyclesharing.util.AppConfig;

public class LoginViewModel {

  public StringProperty login = new SimpleStringProperty("");
  public StringProperty password = new SimpleStringProperty("");
  public StringProperty errorMessage = new SimpleStringProperty("");
  public BooleanProperty loginSuccess = new SimpleBooleanProperty(false);

  private final AuthService authService;

  public LoginViewModel() {
    this.authService = AppConfig.authService();
  }

  public void login() {
    String loginValue = login.get() == null ? "" : login.get().trim();
    String passwordValue = password.get() == null ? "" : password.get().trim();

    try {
      User currentUser = authService.authenticate(loginValue, passwordValue);
      errorMessage.set("");
      loginSuccess.set(true);
      System.out.println("Успішна авторизація: " + currentUser.getLogin());
    } catch (AuthException e) {
      errorMessage.set(e.getMessage());
    }
  }
}