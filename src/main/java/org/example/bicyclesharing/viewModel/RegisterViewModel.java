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
import javafx.scene.layout.VBox;
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
  private VBox registrationPane;
  @FXML
  private VBox confirmationPane;
  @FXML
  private Button confirmCodeButton;

  private int sentCode;
  private User tempUser;

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

    registerButton.setOnAction(event -> onRegister());
    confirmCodeButton.setOnAction(e -> onConfirmCode());
  }


  private void onRegister() {
    clearErrors();

    String loginValue = loginField.getText() == null ? "" : loginField.getText().trim();
    String passwordValue = passwordField.getText() == null ? "" : passwordField.getText().trim();
    String emailValue = emailField.getText() == null ? "" : emailField.getText().trim();

    try {
      tempUser = new User(loginValue, passwordValue, emailValue, Role.CLIENT);
    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        String msg = String.join("\n", messages);
        switch (field) {
          case "login" -> loginErrorLabel.setText(msg);
          case "password" -> passwordErrorLabel.setText(msg);
          case "email" -> emailErrorLabel.setText(msg);
        }
      });
      return;
    }

    try {
      sentCode = verificationService.sendVerificationCode(emailValue);

      registrationPane.setVisible(false);
      confirmationPane.setVisible(true);
      emailCodeField.requestFocus();

    } catch (Exception ex) {
      emailErrorLabel.setText("Не вдалося відправити код!");
      ex.printStackTrace();
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
  private void onConfirmCode() {
    emailErrorLabel.setText("");
    String code = emailCodeField.getText() == null ? "" : emailCodeField.getText().trim();
    if (!String.valueOf(sentCode).equals(code)) {
      emailCodeErrorLabel.setText("Невірний код!");
      return;
    }

    try {
      userService.add(tempUser);
      System.out.println("Користувач зареєстрований: " + tempUser);
    } catch (CustomEntityValidationExeption e) {
      e.getErrors().forEach((field, messages) -> {
        emailCodeErrorLabel.setText(String.join("\n", messages));
      });
    }
  }
}