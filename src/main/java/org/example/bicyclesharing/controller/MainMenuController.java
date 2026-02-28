package org.example.bicyclesharing.controller;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenuController {

  @FXML
  private Button closeButton;
  @FXML
  private Button minimizeButton;
  @FXML
  private Button fullSizeButton;
  @FXML
  private VBox sidebar;

  @FXML
  public void initialize()
  {
    double expandedWidth = 180;
    double collapsedWidth = 60;

    sidebar.setOnMouseEntered(e -> {
      Timeline expand = new Timeline(
          new KeyFrame(Duration.millis(300),
              new KeyValue(sidebar.prefWidthProperty(), expandedWidth, Interpolator.EASE_BOTH)
          )
      );
      expand.play();
    });
    sidebar.setOnMouseExited(e -> {
      Timeline collapse = new Timeline(
          new KeyFrame(Duration.millis(300),
              new KeyValue(sidebar.prefWidthProperty(), collapsedWidth, Interpolator.EASE_BOTH)
          )
      );
      collapse.play();
    });
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

  public void fullSize()
  {
    Stage stage = (Stage) fullSizeButton.getScene().getWindow();
    stage.setMaximized(!stage.isMaximized());
  }


}
