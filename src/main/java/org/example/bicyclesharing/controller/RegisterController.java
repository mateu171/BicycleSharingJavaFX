package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.RegisterViewModel;

public class RegisterController implements Navigatable {

  @FXML private TextField loginField;
  @FXML private TextField passwordField;
  @FXML private TextField emailField;
  @FXML private TextField emailCodeField;

  @FXML private Label loginErrorLabel;
  @FXML private Label passwordErrorLabel;
  @FXML private Label emailErrorLabel;
  @FXML private Label emailCodeErrorLabel;
  @FXML private Label registerTitle;
  @FXML private Label confirmEmailTitle;

  @FXML private VBox confirmationPane;
  @FXML private VBox registrationPane;
  @FXML private Button registerButton;
  @FXML private Button loginButton;
  @FXML private Button confirmCodeButton;

  private RegisterViewModel viewModel;
  private NavigationService navigation;

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

    loginField.promptTextProperty().bind(viewModel.loginPromptText);
    registerTitle.textProperty().bind(viewModel.registerTitleText);
    passwordField.promptTextProperty().bind(viewModel.passwordPromptText);
    registerButton.textProperty().bind(viewModel.registerButtonText);
    loginButton.textProperty().bind(viewModel.alreadyAccountText);
    emailField.promptTextProperty().bind(viewModel.emailPromptText);
    emailCodeField.promptTextProperty().bind(viewModel.codePromptText);
    confirmCodeButton.textProperty().bind(viewModel.confirmButtonText);
    confirmEmailTitle.textProperty().bind(viewModel.confirmEmailText);

    loginField.textProperty().addListener((obs, oldText, newText) -> viewModel.loginError.set(""));
    passwordField.textProperty().addListener((obs, oldText, newText) -> viewModel.passwordError.set(""));
    emailField.textProperty().addListener((obs, oldText, newText) -> viewModel.emailError.set(""));
    emailCodeField.textProperty().addListener((obs, oldText, newText) -> viewModel.emailCodeError.set(""));

    loginField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.loginError.set("");
    });
    passwordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.passwordError.set("");
    });
    emailField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.emailError.set("");
    });
    emailCodeField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.emailCodeError.set("");
    });

    confirmationPane.visibleProperty()
        .bind(viewModel.confirmationVisible);
    registrationPane.visibleProperty()
        .bind(viewModel.registrationVisible);

    viewModel.onRegistrationSuccess = () -> {
      openLoginWindow();
    };
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
  private void openLoginWindow() {
    navigation.load("/org/example/bicyclesharing/presentation/LoginView.fxml");
  }

  @Override
  public void setNavigation(NavigationService navigation) {
    this.navigation = navigation;
  }
}
