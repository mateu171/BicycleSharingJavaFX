package org.example.bicyclesharing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.MainController;
import org.example.bicyclesharing.viewModel.RegisterViewModel;

public class HelloApplication extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
        "/org/example/bicyclesharing/presentation/MainView.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    scene.setFill(Color.TRANSPARENT);
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.setScene(scene);
    stage.show();

    MainController controller = fxmlLoader.getController();
    controller.showRegister();
  }
}
