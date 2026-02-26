package org.example.bicyclesharing.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.RegisterViewModel;

public class RegisterController {

  @FXML private TextField loginField;
  @FXML private TextField passwordField;
  @FXML private TextField emailField;
  @FXML private TextField emailCodeField;

  @FXML private Label loginErrorLabel;
  @FXML private Label passwordErrorLabel;
  @FXML private Label emailErrorLabel;
  @FXML private Label emailCodeErrorLabel;

  @FXML private VBox confirmationPane;
  @FXML private VBox registrationPane;

  private RegisterViewModel viewModel;

  @FXML
  private void initialize() {
    viewModel = new RegisterViewModel(
        AppConfig.userService(),
        AppConfig.verificationService()
    );

    loginField.textProperty().bindBidirectional(viewModel.login);
    passwordField.textProperty().bindBidirectional(viewModel.password);
    emailField.textProperty().bindBidirectional(viewModel.email);
    emailCodeField.textProperty().bindBidirectional(viewModel.emailCode);

    loginErrorLabel.textProperty().bind(viewModel.loginError);
    passwordErrorLabel.textProperty().bind(viewModel.passwordError);
    emailErrorLabel.textProperty().bind(viewModel.emailError);
    emailCodeErrorLabel.textProperty().bind(viewModel.emailCodeError);

    confirmationPane.visibleProperty()
        .bind(viewModel.confirmationVisible);
    registrationPane.visibleProperty()
        .bind(viewModel.registrationVisible);
  }

  @FXML
  private void onRegister() {
    viewModel.register();
  }

  @FXML
  private void onConfirmCode() {
    viewModel.confirmCode();
  }

  @FXML
  private void openLoginWindow(javafx.event.ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bicyclesharing/presentation/LoginView.fxml"));
    Scene scene = new Scene(loader.load());

    Stage stage = new Stage();
    stage.setScene(scene);
    stage.initStyle(StageStyle.UNDECORATED);
    stage.show();

    Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    currentStage.close();
  }

}
