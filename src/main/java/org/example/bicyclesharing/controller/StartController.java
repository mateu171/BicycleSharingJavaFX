package org.example.bicyclesharing.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class StartController {

  @FXML
  private StackPane contentPane;
  @FXML
  private Button closeButton;
  @FXML
  private Button minimizeButton;
  @FXML
  private HBox titleBar;

  public void showLogin() {
    load("/org/example/bicyclesharing/presentation/LoginView.fxml");
  }

  public void showRegister() {
    load("/org/example/bicyclesharing/presentation/RegisterView.fxml");
  }


  private void load(String path) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
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

  }
