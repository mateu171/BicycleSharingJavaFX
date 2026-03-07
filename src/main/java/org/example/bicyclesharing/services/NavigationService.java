package org.example.bicyclesharing.services;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.MainMenuController;
import org.example.bicyclesharing.controller.Navigatable;
import org.example.bicyclesharing.domain.Impl.User;

public class NavigationService {

  private final StackPane contentPane;

  public NavigationService(StackPane contentPane) {
    this.contentPane = contentPane;
  }

  public void load(String fxmlPath) {
    try {

      FXMLLoader loader = new FXMLLoader(
          getClass().getResource(fxmlPath)
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

  public void openWindow(String fxmlPath, Object data) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
      Parent view = loader.load();

      Object controller = loader.getController();
      if (controller instanceof Navigatable navigatable) {
        navigatable.setNavigation(this);
      }

      if (controller instanceof MainMenuController mainMenuController && data instanceof User user) {
        mainMenuController.setCurrentUser(user);
      }

      Stage stage = new Stage();
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.setScene(new Scene(view));
      stage.getScene().setFill(Color.TRANSPARENT);
      stage.show();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
