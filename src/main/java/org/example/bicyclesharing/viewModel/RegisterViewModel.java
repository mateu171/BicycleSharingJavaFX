package org.example.bicyclesharing.viewModel;

import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
  @FXML
  private Button loginButton;

  private final StringProperty login = new SimpleStringProperty();
  private final StringProperty password = new SimpleStringProperty();
  private final StringProperty email = new SimpleStringProperty();
  private final StringProperty emailCode = new SimpleStringProperty();

  private final UserService userService;

  public RegisterViewModel() {
    this.userService = AppConfig.userService();
  }

  @FXML
  private void initialize() {
    loginField.textProperty().bindBidirectional(login);
    passwordField.textProperty().bindBidirectional(password);
    emailField.textProperty().bindBidirectional(email);
    emailCodeField.textProperty().bindBidirectional(emailCode);

    registerButton.setOnAction(event -> onRegister());
  }

  private void onRegister() {
    String loginValue = login.get();
    String passwordValue = password.get();
    String emailValue = email.get();
    String emailCodeValue = emailCode.get();

    if (userService.existsByLogin(loginValue)) {
      System.out.println("Користувач з таким логіном вже існує!");
      return;
    }

    User user = new User(loginValue, passwordValue, emailValue, Role.CLIENT);
    userService.add(user);

    System.out.println("Користувач зареєстрований: " + user);
  }

  public void openLoginWindow(ActionEvent event) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/bicyclesharing/presentation/LoginView.fxml"));
      Scene scene = new Scene(fxmlLoader.load());
      Stage stage = new Stage();
      stage.setTitle("Вхід у систему");
      stage.setScene(scene);
      stage.show();

      ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}