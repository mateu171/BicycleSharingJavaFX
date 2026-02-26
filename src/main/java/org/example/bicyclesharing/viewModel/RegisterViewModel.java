package org.example.bicyclesharing.viewModel;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.services.VerificationService;
public class RegisterViewModel {

  private final UserService userService;
  private final VerificationService verificationService;

  public StringProperty login = new SimpleStringProperty("");
  public StringProperty password = new SimpleStringProperty("");
  public StringProperty email = new SimpleStringProperty("");
  public StringProperty emailCode = new SimpleStringProperty("");

  public StringProperty loginError = new SimpleStringProperty("");
  public StringProperty passwordError = new SimpleStringProperty("");
  public StringProperty emailError = new SimpleStringProperty("");
  public StringProperty emailCodeError = new SimpleStringProperty("");

  public BooleanProperty confirmationVisible = new SimpleBooleanProperty(false);
  public BooleanProperty registrationVisible = new SimpleBooleanProperty(true);

  private int sentCode;
  private User tempUser;

  public RegisterViewModel(UserService userService,
      VerificationService verificationService) {
    this.userService = userService;
    this.verificationService = verificationService;
  }

  public void register() {
    clearErrors();

    try {
      tempUser = new User(
          login.get(),
          password.get(),
          email.get(),
          Role.CLIENT
      );

    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        String msg = String.join("\n", messages);
        switch (field) {
          case "login" -> loginError.set(msg);
          case "password" -> passwordError.set(msg);
          case "email" -> emailError.set(msg);
        }
      });
      return;
    }

    try {
      sentCode = verificationService.sendVerificationCode(email.get());
      registrationVisible.set(false);
      confirmationVisible.set(true);
    } catch (Exception ex) {
      emailError.set("Не вдалося відправити код!");
    }
  }

  public void confirmCode() {
    if (!String.valueOf(sentCode).equals(emailCode.get())) {
      emailCodeError.set("Невірний код!");
      return;
    }

    userService.add(tempUser);
  }

  private void clearErrors() {
    loginError.set("");
    passwordError.set("");
    emailError.set("");
    emailCodeError.set("");
  }
}