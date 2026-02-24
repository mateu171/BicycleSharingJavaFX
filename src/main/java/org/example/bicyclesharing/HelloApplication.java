package org.example.bicyclesharing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import org.example.bicyclesharing.viewModel.RegisterViewModel;

public class HelloApplication extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
        "/org/example/bicyclesharing/presentation/RegisterView.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 960/2, 900/2);
    RegisterViewModel controller = fxmlLoader.getController();
    stage.setScene(scene);
    stage.show();
  }
}
