package org.example.bicyclesharing.controller;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.bicyclesharing.util.LocalizationManager;

public class StartController {

  @FXML
  private StackPane contentPane;
  @FXML
  private Button closeButton;
  @FXML
  private Button minimizeButton;

  private String currentView;

  public void showLogin() {
    load("/org/example/bicyclesharing/presentation/LoginView.fxml");
  }

  public void showRegister() {
    load("/org/example/bicyclesharing/presentation/RegisterView.fxml");
  }


  private void load(String path) {
    currentView = path;
    try {
      ResourceBundle bundle = LocalizationManager.getBundle();
      FXMLLoader loader = new FXMLLoader(getClass().getResource(path),bundle);
      Parent view = loader.load();

      Object controller = loader.getController();
      if (controller instanceof LoginController loginController) {
        loginController.setMainController(this);
      }
      if (controller instanceof RegisterController registerController) {
        registerController.setMainController(this);
      }

      contentPane.getChildren().setAll(view);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void closeWindow()
  {
    Stage stage = (Stage) closeButton.getScene().getWindow();
    stage.close();
  }

  public void minimizeWindow()
  {
    Stage stage = (Stage) minimizeButton.getScene().getWindow();
    stage.setIconified(true);
  }

  public void switchLanguage() {

    Locale current = LocalizationManager.getBundle().getLocale();

    if (current.getLanguage().equals("ua")) {
      LocalizationManager.setLocale("en");
    } else {
      LocalizationManager.setLocale("ua");
    }
      load(currentView);
  }
}
