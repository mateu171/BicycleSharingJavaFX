package org.example.bicyclesharing.viewModel;

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

  public StringProperty loginErrorKey = new SimpleStringProperty("");
  public StringProperty passwordErrorKey = new SimpleStringProperty("");
  public StringProperty emailErrorKey = new SimpleStringProperty("");
  public StringProperty emailCodeErrorKey = new SimpleStringProperty("");

  public BooleanProperty confirmationVisible = new SimpleBooleanProperty(false);
  public BooleanProperty registrationVisible = new SimpleBooleanProperty(true);

  public final StringProperty loginPromptText = LocalizationManager.getStringProperty("register.login");
  public final StringProperty registerTitleText = LocalizationManager.getStringProperty("register.title");
  public final StringProperty passwordPromptText = LocalizationManager.getStringProperty("register.password");
  public final StringProperty registerButtonText = LocalizationManager.getStringProperty("register.signUp");
  public final StringProperty alreadyAccountText = LocalizationManager.getStringProperty("register.alreadyAccount");
  public final StringProperty emailPromptText = LocalizationManager.getStringProperty("register.email");
  public final StringProperty confirmEmailText = LocalizationManager.getStringProperty("register.confirmEmail");
  public final StringProperty codePromptText = LocalizationManager.getStringProperty("register.code");
  public final StringProperty confirmButtonText = LocalizationManager.getStringProperty("register.confirmButton");

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
        loginErrorKey.set("error.login.exists");
        return;
      }

    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, keys) -> {
        StringProperty targetProperty;
        switch (field) {
          case "login" -> targetProperty = loginErrorKey;
          case "password" -> targetProperty = passwordErrorKey;
          case "email" -> targetProperty = emailErrorKey;
          default -> targetProperty = null;
        }

        if (targetProperty != null && !keys.isEmpty()) {
          targetProperty.set(keys.get(0));
        }
      });
      return;
    }

    try {
      sentCode = verificationService.sendVerificationCode(email.get());
      registrationVisible.set(false);
      confirmationVisible.set(true);
    } catch (Exception ex) {
      emailErrorKey.set("error.email.send_failed");
    }
  }

  public void confirmCode() {
    if (!String.valueOf(sentCode).equals(emailCode.get())) {
      emailCodeErrorKey.set("error.email.code_invalid");
      return;
    }

    userService.add(tempUser);

    registrationVisible.set(false);
    confirmationVisible.set(false);

    if(onRegistrationSuccess != null)
    onRegistrationSuccess.run();
  }

  private void clearErrors() {
    loginErrorKey.set("");
    passwordErrorKey.set("");
    emailErrorKey.set("");
    emailCodeErrorKey.set("");
  }
}