package org.example.bicyclesharing.controller.view.admin.modalController;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class MapPickerController {

  @FXML
  private WebView mapWebView;

  private CoordinateSelectedListener listener;

  public interface CoordinateSelectedListener {
    void onSelected(double latitude, double longitude);
  }

  public void setListener(CoordinateSelectedListener listener) {
    this.listener = listener;
  }

  @FXML
  public void initialize() {
    WebEngine engine = mapWebView.getEngine();

    var resource = getClass().getResource("/org/example/bicyclesharing/html/map-picker.html");
    System.out.println("HTML resource = " + resource);

    if (resource == null) {
      throw new RuntimeException("map-picker.html not found");
    }

    engine.load(resource.toExternalForm());

    engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
      if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
        JSObject window = (JSObject) engine.executeScript("window");
        window.setMember("javaConnector", this);
        System.out.println("javaConnector attached");
      }
    });
  }

  public void selectCoordinate(double latitude, double longitude) {
    System.out.println("selectCoordinate called: " + latitude + ", " + longitude);
    System.out.println("listener = " + listener);

    if (listener != null) {
      listener.onSelected(latitude, longitude);
    }

    Stage stage = (Stage) mapWebView.getScene().getWindow();
    stage.close();
  }
}