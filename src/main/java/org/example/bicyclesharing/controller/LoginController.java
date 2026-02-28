package org.example.bicyclesharing.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.viewModel.LoginViewModel;

public class LoginController {

  @FXML private TextField loginField;
  @FXML private TextField passwordField;
  @FXML private Label errorMessage;
  private LoginViewModel viewModel;
  private StartController startController;

  @FXML
  private void initialize() {
    viewModel = new LoginViewModel();

    loginField.textProperty().bindBidirectional(viewModel.login);
    passwordField.textProperty().bindBidirectional(viewModel.password);
    errorMessage.textProperty().bind(viewModel.errorMessage);

    loginField.textProperty().addListener((obs, oldText, newText) -> viewModel.errorMessage.set(""));
    passwordField.textProperty().addListener((obs, oldText, newText) -> viewModel.errorMessage.set(""));

    loginField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.errorMessage.set("");
    });
    passwordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.errorMessage.set("");
    });

    viewModel.loginSuccess.addListener((obs,odlVal,newVal) ->
    {
      if(newVal)
      {
        openMainMenu();
      }
    });

  }
  @FXML
  private void onLogin()
  {
    viewModel.login();
  }

  public void setMainController(StartController startController) {
    this.startController = startController;
  }
  @FXML
  private void openRegisterWindow() {
    startController.showRegister();
  }
  private  void openMainMenu()
  {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/org/example/bicyclesharing/presentation/MainMenuView.fxml")
      );

      Parent root = loader.load();
      Stage stage = new Stage();
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.setScene(new Scene(root));
      stage.show();

      MainMenuController controller = loader.getController();
      controller.setCurrentUser(viewModel.getCurrentUser());
      ((Stage) loginField.getScene().getWindow()).close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}