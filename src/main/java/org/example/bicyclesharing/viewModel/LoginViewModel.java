package org.example.bicyclesharing.viewModel;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.AuthException;
import org.example.bicyclesharing.services.AuthService;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.util.AppConfig;

public class LoginViewModel {

  @FXML
  private TextField loginField;

  @FXML
  private TextField passwordField;

  @FXML
  private Button loginButton;
  @FXML
  private Button openRegisterWindow;

  private final UserService userService;
  private final AuthService authService;

  public LoginViewModel() {
    this.userService = AppConfig.userService();
    this.authService = AppConfig.authService();
  }

  @FXML
  private void initialize() {
    loginButton.setOnAction(event -> onLogin());
  }

  public void onLogin() {
    String login = loginField.getText();
    String password = passwordField.getText();

    try {
      User currentUser = authService.authenticate(login, password);
      System.out.println("Успішна авторизація");
    } catch (AuthException exception) {
      System.out.println(exception.getMessage());
    }
  }

  public void openLoginRegister(ActionEvent event) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(
          getClass().getResource("/org/example/bicyclesharing/presentation/RegisterView.fxml"));
      Scene scene = new Scene(fxmlLoader.load());
      RegisterViewModel registerViewModel = fxmlLoader.getController();
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
