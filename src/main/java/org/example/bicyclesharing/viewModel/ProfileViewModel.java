package org.example.bicyclesharing.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
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

  private User tempUser;
  private User curentUser;

  public ProfileViewModel(UserService userService,User currentUser) {
    this.userService = userService;
    this.curentUser = currentUser;

    login.set(currentUser.getLogin());
    password.set(currentUser.getPassword());
    email.set(currentUser.getEmail());
  }

  public void update()
  {
      clearErrors();

    try {
      tempUser = new User(
          login.get(),
          password.get(),
          email.get(),
          Role.CLIENT
      );

      userService.update(tempUser);

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
