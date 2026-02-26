package org.example.bicyclesharing.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.viewModel.LoginViewModel;

public class LoginController {

  @FXML private TextField loginField;
  @FXML private TextField passwordField;
  @FXML private Label errorMessage;
  private LoginViewModel viewModel;

  @FXML
  private void initialize() {
    viewModel = new LoginViewModel();

    loginField.textProperty().bindBidirectional(viewModel.login);
    passwordField.textProperty().bindBidirectional(viewModel.password);
    errorMessage.textProperty().bind(viewModel.errorMessage);

  }
  @FXML
  private void onLogin()
  {
    viewModel.login();
  }

  @FXML
  private void openRegisterWindow(ActionEvent event) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(
          getClass().getResource("/org/example/bicyclesharing/presentation/RegisterView.fxml"));
      Scene scene = new Scene(fxmlLoader.load());

      Stage stage = new Stage();
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setScene(scene);
      stage.show();

      Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
      currentStage.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}