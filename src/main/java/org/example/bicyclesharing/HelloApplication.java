package org.example.bicyclesharing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.viewModel.RegisterViewModel;

public class HelloApplication extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
        "/org/example/bicyclesharing/presentation/RegisterView.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.initStyle(StageStyle.UNDECORATED);
    stage.setScene(scene);
    stage.show();
  }
}
