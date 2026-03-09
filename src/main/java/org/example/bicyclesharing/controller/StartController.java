package org.example.bicyclesharing.controller;

import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.util.LocalizationManager;

public class StartController {

  @FXML
  private StackPane contentPane;
  @FXML
  private Button closeButton;
  @FXML
  private Button minimizeButton;

  private NavigationService navigation;

  public void showLogin() {
    navigation.load("/org/example/bicyclesharing/presentation/LoginView.fxml");
  }

  public void showRegister() {
    navigation.load("/org/example/bicyclesharing/presentation/RegisterView.fxml");
  }

  @FXML
  private void initialize() {
    navigation = new NavigationService(contentPane);
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

    Locale current = LocalizationManager.getLocale();

    if (current.getLanguage().equals("uk")) {
      LocalizationManager.setLocale("en");
    } else {
      LocalizationManager.setLocale("uk");
    }
  }
}
