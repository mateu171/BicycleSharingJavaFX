package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.ProfileViewModel;

public class ProfileController {

  @FXML private TextField loginField;
  @FXML private TextField passwordField;
  @FXML private TextField emailField;

  @FXML private Label loginErrorLabel;
  @FXML private Label passwordErrorLabel;
  @FXML private Label emailErrorLabel;

  private ProfileViewModel viewModel;
  private MainMenuController mainMenuController;
  private User currentUser;

  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;

    viewModel = new ProfileViewModel(
        AppConfig.userService(),
        currentUser
    );
    bindFields();
    fillFields();
  }

  public void setMainController(MainMenuController mainMenuController) {
    this.mainMenuController = mainMenuController;
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

    loginErrorLabel.textProperty().bind(viewModel.loginError);
    passwordErrorLabel.textProperty().bind(viewModel.passwordError);
    emailErrorLabel.textProperty().bind(viewModel.emailError);
  }

  private void fillFields() {
    loginField.setText(currentUser.getLogin());
    passwordField.setText(currentUser.getPassword());
    emailField.setText(currentUser.getEmail());
  }

}
