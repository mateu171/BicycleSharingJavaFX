package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.viewModel.LoginViewModel;

public class LoginController implements Navigatable{

  @FXML private TextField loginField;
  @FXML private TextField passwordField;
  @FXML private Label errorMessage;
  @FXML private Label title;
  @FXML private Button registerButton;
  @FXML private Button loginButton;

  private LoginViewModel viewModel;
  private NavigationService navigation;

  @FXML
  private void initialize() {
    viewModel = new LoginViewModel();

    loginField.textProperty().bindBidirectional(viewModel.login);
    title.textProperty().bind(viewModel.titleText);
    loginField.promptTextProperty().bind(viewModel.loginPromptText);
    passwordField.promptTextProperty().bind(viewModel.passwordPromptText);
    registerButton.textProperty().bind(viewModel.registerButtonText);
    loginButton.textProperty().bind(viewModel.signInButtonText);

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

  @FXML
  private void openRegisterWindow() {
    navigation.load("/org/example/bicyclesharing/presentation/RegisterView.fxml");
  }
  private  void openMainMenu()
  {
    navigation.load("/org/example/bicyclesharing/presentation/MainMenuView.fxml");
  }

  @Override
  public void setNavigation(NavigationService navigation) {
    this.navigation = navigation;
  }
}