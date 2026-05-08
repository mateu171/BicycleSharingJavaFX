package org.example.bicyclesharing.viewModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.AuthException;
import org.example.bicyclesharing.services.AuthService;
import org.example.bicyclesharing.util.LocalizationManager;

public class LoginViewModel {

  private final AuthService authService;

  private User currentUser;

  private final StringProperty login =
      new SimpleStringProperty("");

  private final StringProperty password =
      new SimpleStringProperty("");

  private final StringProperty errorText =
      new SimpleStringProperty("");

  private final BooleanProperty authenticated =
      new SimpleBooleanProperty(false);

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("login.title");

  private final StringProperty loginPromptText =
      LocalizationManager.getStringProperty("login.login");

  private final StringProperty passwordPromptText =
      LocalizationManager.getStringProperty("login.password");

  private final StringProperty signInButtonText =
      LocalizationManager.getStringProperty("login.signIn");

  public LoginViewModel(AuthService authService) {
    this.authService = authService;
  }

  public void login() {
    clearState();

    String loginValue = safe(login.get());
    String passwordValue = safe(password.get());

    try {
      currentUser = authService.authenticate(loginValue, passwordValue);
      authenticated.set(true);

    } catch (AuthException e) {
      errorText.set(
          LocalizationManager.getStringByKey(e.getMessage())
      );
    }
  }

  private void clearState() {
    authenticated.set(false);
    errorText.set("");
  }

  private String safe(String value) {
    return value == null ? "" : value.trim();
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public StringProperty loginProperty() {
    return login;
  }

  public StringProperty passwordProperty() {
    return password;
  }

  public StringProperty errorTextProperty() {
    return errorText;
  }

  public BooleanProperty authenticatedProperty() {
    return authenticated;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty loginPromptTextProperty() {
    return loginPromptText;
  }

  public StringProperty passwordPromptTextProperty() {
    return passwordPromptText;
  }

  public StringProperty signInButtonTextProperty() {
    return signInButtonText;
  }
}