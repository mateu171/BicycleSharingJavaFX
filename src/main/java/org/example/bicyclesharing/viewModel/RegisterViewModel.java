package org.example.bicyclesharing.viewModel;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.services.VerificationService;
import org.example.bicyclesharing.util.AppConfig;

public class RegisterViewModel {

  @FXML
  private TextField loginField;
  @FXML
  private TextField passwordField;
  @FXML
  private TextField emailField;
  @FXML
  private TextField emailCodeField;
  @FXML
  private Label loginErrorLabel;
  @FXML
  private Label passwordErrorLabel;
  @FXML
  private Label emailErrorLabel;
  @FXML
  private Label emailCodeErrorLabel;
  @FXML
  private Button registerButton;
  @FXML
  private Button sendCodeButton;

  private int sentCode;

  private final StringProperty login = new SimpleStringProperty();
  private final StringProperty password = new SimpleStringProperty();
  private final StringProperty email = new SimpleStringProperty();
  private final StringProperty emailCode = new SimpleStringProperty();

  private final UserService userService;
  private final VerificationService verificationService;

  public RegisterViewModel() {
    this.userService = AppConfig.userService();
    this.verificationService = AppConfig.verificationService();
  }

  @FXML
  private void initialize() {
    loginField.textProperty().bindBidirectional(login);
    passwordField.textProperty().bindBidirectional(password);
    emailField.textProperty().bindBidirectional(email);
    emailCodeField.textProperty().bindBidirectional(emailCode);

    emailCodeField.setDisable(true);

    registerButton.setOnAction(event -> onRegister());

    sendCodeButton.disableProperty().bind(
        Bindings.createBooleanBinding(
            () -> !isValidEmail(emailField.getText()),
            emailField.textProperty()
        )
    );
  }

  public void onSendCodeToEmail() {
    emailErrorLabel.setText("");
    String emailValue = email.get() == null ? "" : email.get().trim();

    if (!isValidEmail(emailValue)) {
      emailErrorLabel.setText("Некоректний формат email");
      return;
    }

    try {
      sentCode = verificationService.sendVerificationCode(emailValue);
      emailErrorLabel.setText("Код надіслано на ваш email!");
      emailCodeField.setDisable(false);
      emailCodeField.requestFocus();
    } catch (Exception e) {
      emailErrorLabel.setText("Не вдалося відправити код!");
      e.printStackTrace();
    }
  }

  private void onRegister() {
    clearErrors();

    boolean hasErrors = false;

    String loginValue = login.get();
    String passwordValue = password.get();
    String emailValue = email.get();
    String emailCodeValue = emailCode.get();

    if (userService.existsByLogin(loginValue)) {
      loginErrorLabel.setText("Користувач з таким логіном вже існує!");
      hasErrors = true;
    }

    if (!emailCodeField.isDisabled()) {
      if (emailCodeValue == null || emailCodeValue.trim().isEmpty()) {
        emailCodeErrorLabel.setText("Введіть код підтвердження!");
        hasErrors = true;
      } else if (!emailCodeValue.equals(String.valueOf(sentCode))) {
        emailCodeErrorLabel.setText("Невірний код підтвердження!");
        hasErrors = true;
      }
    }

    if (hasErrors) {
      return;
    }

    try {
      User user = new User(loginValue, passwordValue, emailValue, Role.CLIENT);
      userService.add(user);
      System.out.println("Користувач зареєстрований: " + user);
    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        String msg = String.join("\n", messages);
        switch (field) {
          case "login" -> loginErrorLabel.setText(msg);
          case "password" -> passwordErrorLabel.setText(msg);
          case "email" -> emailErrorLabel.setText(msg);
          case "emailCode" -> emailCodeErrorLabel.setText(msg);
        }
      });
    }
  }

  public void openLoginWindow(ActionEvent event) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(
          getClass().getResource("/org/example/bicyclesharing/presentation/LoginView.fxml"));
      Scene scene = new Scene(fxmlLoader.load());
      Stage stage = new Stage();
      stage.setScene(scene);
      stage.initStyle(StageStyle.UNDECORATED);
      stage.show();
      ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void clearErrors() {
    loginErrorLabel.setText("");
    emailErrorLabel.setText("");
    passwordErrorLabel.setText("");
    emailCodeErrorLabel.setText("");
  }

  private boolean isValidEmail(String email) {
    return email != null && email.matches("^[a-zA-Z0-9]+@gmail\\.com$");
  }
}