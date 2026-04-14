package org.example.bicyclesharing.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

  public static <T> void openModal(String fxmlPath,ControllerInitializer<T> initializer)
      throws IOException {
    FXMLLoader loader = new FXMLLoader(WindowUtil.class.getResource(fxmlPath));

    Parent root = loader.load();
    T controller = loader.getController();

    if(initializer != null)
      initializer.init(controller);

    Scene scene = new Scene(root);
    scene.setFill(Color.TRANSPARENT);
    scene.getStylesheets().add(WindowUtil.class.getResource("/org/example/bicyclesharing/css/style.css").toExternalForm());

    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.setScene(scene);
    stage.showAndWait();
  }

}
