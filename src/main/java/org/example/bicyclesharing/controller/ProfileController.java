package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.ProfileViewModel;

public class ProfileController extends BaseController{

  @FXML private TextField loginField;
  @FXML private TextField passwordField;
  @FXML private TextField emailField;

  @FXML private Label loginErrorLabel;
  @FXML private Label passwordErrorLabel;
  @FXML private Label emailErrorLabel;
  @FXML private Label successLabel;
  @FXML private Label titleLabel;
  @FXML private Button updateButton;

  private ProfileViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new ProfileViewModel(
        AppConfig.userService(),
        currentUser
    );
    bindFields();
  }

  @FXML
  private void initialize() {
    loginField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.loginError.set("");
    });
    passwordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.passwordError.set("");
    });
    emailField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
      if (isNowFocused) viewModel.emailError.set("");
    });
  }

  public void onUpdate()
  {
    viewModel.update();
  }

  private void bindFields() {
    loginField.textProperty().bindBidirectional(viewModel.login);
    passwordField.textProperty().bindBidirectional(viewModel.password);
    emailField.textProperty().bindBidirectional(viewModel.email);
    loginField.promptTextProperty().bind(viewModel.loginPrompt);
    passwordField.promptTextProperty().bind(viewModel.passwordPrompt);
    emailField.promptTextProperty().bind(viewModel.emailPrompt);

    loginErrorLabel.textProperty().bind(viewModel.loginError);
    passwordErrorLabel.textProperty().bind(viewModel.passwordError);
    emailErrorLabel.textProperty().bind(viewModel.emailError);
    successLabel.textProperty().bind(viewModel.successMessage);
    titleLabel.textProperty().bind(viewModel.titleText);
    updateButton.textProperty().bind(viewModel.updateButtonText);
  }

}
