package org.example.bicyclesharing.viewModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.AuthException;
import org.example.bicyclesharing.services.AuthService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;

public class LoginViewModel {

  public StringProperty login = new SimpleStringProperty("");
  public StringProperty password = new SimpleStringProperty("");
  public StringProperty errorKey = new SimpleStringProperty("");
  public BooleanProperty loginSuccess = new SimpleBooleanProperty(false);

  public final StringProperty titleText = LocalizationManager.getStringProperty("login.title");
  public final StringProperty loginPromptText = LocalizationManager.getStringProperty("login.login");
  public final StringProperty passwordPromptText = LocalizationManager.getStringProperty("login.password");
  public final StringProperty signInButtonText = LocalizationManager.getStringProperty("login.signIn");
  public final StringProperty registerButtonText = LocalizationManager.getStringProperty("login.noAccount");

  private final AuthService authService;
  private User currentUser;

  public LoginViewModel() {
    this.authService = AppConfig.authService();
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public void login() {
    String loginValue = login.get() == null ? "" : login.get().trim();
    String passwordValue = password.get() == null ? "" : password.get().trim();

    try {
      currentUser = authService.authenticate(loginValue, passwordValue);
      errorKey.set("");
      loginSuccess.set(true);
    } catch (AuthException e) {
      errorKey.set(e.getMessage());
    }
  }
}