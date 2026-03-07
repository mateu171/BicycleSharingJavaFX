package org.example.bicyclesharing.util;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SidebarAnimation {

  public static void applyHoverAnimation(VBox sidebar, double expandedWidth, double collapsedWidth) {

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
}