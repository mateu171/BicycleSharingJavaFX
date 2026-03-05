package org.example.bicyclesharing.viewModel;

import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.services.VerificationService;
import org.example.bicyclesharing.util.LocalizationManager;

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
  public Runnable onRegistrationSuccess;

  public RegisterViewModel(UserService userService,
      VerificationService verificationService) {
    this.userService = userService;
    this.verificationService = verificationService;
  }

  public void register() {
    clearErrors();

    try {
      tempUser = User.create(
          login.get(),
          password.get(),
          email.get(),
          Role.CLIENT
      );

      if(userService.existsByLogin(tempUser.getLogin()))
      {
        loginError.set(LocalizationManager.getStringByKey("error.login.exists"));
        return;
      }

    } catch (CustomEntityValidationExeption e) {
      ResourceBundle bundle = LocalizationManager.getBundle();
      e.getErrors().forEach((field, messages) -> {
        String msg = messages.stream()
            .map(bundle::getString)
            .collect(Collectors.joining("\n"));
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
      emailError.set(LocalizationManager.getStringByKey("error.email.send_failed"));
    }
  }

  public void confirmCode() {
    if (!String.valueOf(sentCode).equals(emailCode.get())) {
      emailCodeError.set(LocalizationManager.getStringByKey("error.email.code_invalid"));
      return;
    }

    userService.add(tempUser);

    registrationVisible.set(false);
    confirmationVisible.set(false);

    if(onRegistrationSuccess != null)
    onRegistrationSuccess.run();
  }

  private void clearErrors() {
    loginError.set("");
    passwordError.set("");
    emailError.set("");
    emailCodeError.set("");
  }
}