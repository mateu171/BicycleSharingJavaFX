package org.example.bicyclesharing.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class MainController {

  @FXML
  private StackPane contentPane;

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
  }
