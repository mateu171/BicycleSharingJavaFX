package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.bicyclesharing.viewModel.LoginViewModel;

public class LoginController {

  @FXML private TextField loginField;
  @FXML private TextField passwordField;
  @FXML private Label errorMessage;
  private LoginViewModel viewModel;
  private MainController mainController;

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

  }
  @FXML
  private void onLogin()
  {
    viewModel.login();
  }

  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }

  @FXML
  private void openRegisterWindow() {
    mainController.showRegister();
  }
}