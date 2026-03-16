package org.example.bicyclesharing.controller.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.util.LocalizationManager;
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
    errorMessage.textProperty().bind(
        Bindings.createStringBinding(
            () -> LocalizationManager.getStringByKey(viewModel.errorKey.get()),
            viewModel.errorKey,
            LocalizationManager.localeProperty()
        )
    );

    loginField.textProperty().addListener((obs, oldText, newText) -> viewModel.errorKey.set(""));
    passwordField.textProperty().addListener((obs, oldText, newText) -> viewModel.errorKey.set(""));

    loginField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.errorKey.set("");
    });
    passwordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.errorKey.set("");
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
    navigation.load("/org/example/bicyclesharing/presentation/view/RegisterView.fxml");
  }
  private  void openMainMenu()
  {
    navigation.openWindow("/org/example/bicyclesharing/presentation/window/MainMenuView.fxml",viewModel.getCurrentUser());
    Stage stage = (Stage) loginButton.getScene().getWindow();
    stage.close();
  }

  @Override
  public void setNavigation(NavigationService navigation) {
    this.navigation = navigation;
  }
}