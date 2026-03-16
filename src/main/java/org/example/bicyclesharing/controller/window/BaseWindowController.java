package org.example.bicyclesharing.controller.window;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.util.WindowUtil;

public abstract class BaseWindowController {

  @FXML
  protected StackPane contentPane;
  protected WindowUtil windowUtil;
  protected NavigationService navigationService;

  @FXML
  protected void initialize() {
    navigationService = new NavigationService(contentPane);
    initializeWindow(contentPane);
  }

  protected void initializeWindow(StackPane contentPane) {
    contentPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene != null) {
        newScene.windowProperty().addListener((obsW, oldWindow, newWindow) -> {
          if (newWindow != null) {
            windowUtil = new WindowUtil((Stage) newWindow);
          }
        });
      }
    });
  }
}
