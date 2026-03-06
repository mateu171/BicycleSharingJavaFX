package org.example.bicyclesharing.viewModel;

import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.util.LocalizationManager;

public class ProfileViewModel {

  private final UserService userService;

  public StringProperty login = new SimpleStringProperty("");
  public StringProperty password = new SimpleStringProperty("");
  public StringProperty email = new SimpleStringProperty("");

  public StringProperty loginError = new SimpleStringProperty("");
  public StringProperty passwordError = new SimpleStringProperty("");
  public StringProperty emailError = new SimpleStringProperty("");
  public StringProperty successMessage = new SimpleStringProperty("");

  private User currentUser;
  private String oldLogin;

  public ProfileViewModel(UserService userService,User currentUser) {
    this.userService = userService;
    this.currentUser = currentUser;

    login.set(currentUser.getLogin());
    email.set(currentUser.getEmail());
    oldLogin = currentUser.getLogin();
  }

  public void update() {
    clearErrors();
    successMessage.set("");

    try {
      currentUser.setLogin(login.get());
      currentUser.setEmail(email.get());

      String newPassword = password.get().trim();

      if (!newPassword.isEmpty()) {
        currentUser.changePassword(newPassword);
      }

      if (!currentUser.isValid()) {
        throw new CustomEntityValidationExeption(currentUser.getErrors());
      }

      if(userService.existsByLoginExcept(login.get(), currentUser.getId())) {
        loginError.set(LocalizationManager.getStringByKey("error.login.exists"));
        return;
      }

      userService.update(currentUser);
      successMessage.set(LocalizationManager.getStringByKey("profile.success"));

    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        StringProperty targetProperty;
        switch (field) {
          case "login" -> targetProperty = loginError;
          case "password" -> targetProperty = passwordError;
          case "email" -> targetProperty = emailError;
          default -> targetProperty = null;
        }

        if (targetProperty != null) {
          String fullMessage = messages.stream()
              .map(LocalizationManager::getStringByKey)
              .collect(Collectors.joining("\n"));
          targetProperty.set(fullMessage);
        }
      });
    }
  }
  private void clearErrors() {
    loginError.set("");
    passwordError.set("");
    emailError.set("");
  }
}
