package org.example.bicyclesharing.util;

import javafx.stage.Stage;

public class WindowUtil {
  private Stage stage;

  public WindowUtil(Stage stage) {
    this.stage = stage;
  }

  public Stage getStage() {
    return stage;
  }

  public void minimize() {
      stage.setIconified(true);
  }

  public void close() {
     stage.close();
  }

  public void toggleFullSize() {
     stage.setFullScreen(!stage.isFullScreen());
  }
}
