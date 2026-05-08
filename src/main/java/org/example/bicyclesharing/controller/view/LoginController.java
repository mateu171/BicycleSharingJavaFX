package org.example.bicyclesharing.controller.view;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.window.MainMenuController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.LoginViewModel;

public class LoginController {

  @FXML private Label titleLabel;
  @FXML private TextField loginField;
  @FXML private PasswordField passwordField;
  @FXML private Button signInButton;
  @FXML private Label errorLabel;

  private LoginViewModel viewModel;

  @FXML
  public void initialize() {
    viewModel = new LoginViewModel(
        AppConfig.authService()
    );

    bind();
    setupListeners();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());

    loginField.promptTextProperty().bind(
        viewModel.loginPromptTextProperty()
    );

    passwordField.promptTextProperty().bind(
        viewModel.passwordPromptTextProperty()
    );

    signInButton.textProperty().bind(
        viewModel.signInButtonTextProperty()
    );

    loginField.textProperty().bindBidirectional(
        viewModel.loginProperty()
    );

    passwordField.textProperty().bindBidirectional(
        viewModel.passwordProperty()
    );

    errorLabel.textProperty().bind(
        viewModel.errorTextProperty()
    );

    errorLabel.visibleProperty().bind(
        viewModel.errorTextProperty().isNotEmpty()
    );

    errorLabel.managedProperty().bind(
        errorLabel.visibleProperty()
    );
  }

  private void setupListeners() {
    viewModel.authenticatedProperty().addListener(
        (obs, oldValue, authenticated) -> {
          if (authenticated) {
            openMainMenu(viewModel.getCurrentUser());
          }
        }
    );
  }

  @FXML
  private void onLogin() {
    viewModel.login();
  }

  private void openMainMenu(User currentUser) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource(
              "/org/example/bicyclesharing/presentation/window/MainMenuView.fxml"
          )
      );

      Parent root = loader.load();

      MainMenuController controller = loader.getController();
      controller.setCurrentUser(currentUser);

      Stage stage = new Stage();
      stage.setScene(new Scene(root));
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.show();

      signInButton.getScene().getWindow().hide();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}