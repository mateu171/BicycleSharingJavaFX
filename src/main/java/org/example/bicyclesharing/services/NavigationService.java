package org.example.bicyclesharing.services;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.example.bicyclesharing.controller.Navigatable;

public class NavigationService {

  private final StackPane contentPane;

  public NavigationService(StackPane contentPane) {
    this.contentPane = contentPane;
  }

  public void load(String fxmlPath) {
    try {

      FXMLLoader loader = new FXMLLoader(
          getClass().getResource(fxmlPath)
//          LocalizationManager.getBundle()
      );

      Parent view = loader.load();

      Object controller = loader.getController();

      if (controller instanceof Navigatable navigatable) {
        navigatable.setNavigation(this);
      }

      contentPane.getChildren().setAll(view);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
