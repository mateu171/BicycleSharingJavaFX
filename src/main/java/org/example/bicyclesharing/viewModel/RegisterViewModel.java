package org.example.bicyclesharing.viewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.services.UserService;
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
  private Button registerButton;

  private final UserService userService;

  public RegisterViewModel() {
    this.userService = AppConfig.userService();
  }

  @FXML
  private void initialize() {
    registerButton.setOnAction(event -> onRegister());
  }

  private void onRegister() {
    String login = loginField.getText();
    String password = passwordField.getText();
    String email = emailField.getText();
    String emailCode = emailCodeField.getText();

    if (userService.existsByLogin(login)) {
      System.out.println("Користувач з таким логіном вже існує!");
      return;
    }

    User user = new User(login, password, email, Role.CLIENT);
    userService.add(user);

    System.out.println("Користувач зареєстрований: " + user);
  }
}