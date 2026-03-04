package org.example.bicyclesharing.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.UserService;

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
        loginError.set("Користувач з таким логіном вже існує");
        return;
      }

      userService.update(currentUser);
      successMessage.set("Профіль успішно оновлено!");

    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        String msg = String.join("\n", messages);
        switch (field) {
          case "login" -> loginError.set(msg);
          case "password" -> passwordError.set(msg);
          case "email" -> emailError.set(msg);
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
