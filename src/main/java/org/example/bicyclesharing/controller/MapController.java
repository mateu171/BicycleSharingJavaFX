package org.example.bicyclesharing.controller;

import java.util.List;
import java.util.UUID;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.util.AppConfig;

public class MapController {

  @FXML
  private WebView webView;
  private BicycleService bicycleService;

  @FXML
  public void initialize() {
   bicycleService = AppConfig.bicycleService();
    WebEngine engine = webView.getEngine();

    var url = getClass().getResource("/org/example/bicyclesharing/map.html");
    engine.load(url.toExternalForm());

    engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {

      if (newState == Worker.State.SUCCEEDED) {
        System.out.println("WebView loaded!");
        JSObject window = (JSObject) engine.executeScript("window");
        window.setMember("javaApp", this);
        Platform.runLater(() -> loadBicycles());
      }
    });

  }

  public void rentBike(UUID id) {

    System.out.println("Велосипед орендовано: " + id);

  }

  private void loadBicycles() {
    List<Bicycle> bicycles = bicycleService.getAll();
    StringBuilder js = new StringBuilder();

    for (Bicycle bike : bicycles) {
      js.append(String.format(
          java.util.Locale.US,
          "addBike(%f,%f,'%s',%f,'%s');",
          bike.getLatitude(),
          bike.getLongitude(),
          bike.getModel().replace("'", "\\'"),
          bike.getPricePerHour(),
          bike.getId().toString()
      ));
    }

    webView.getEngine().executeScript(js.toString());
  }

  public void showBikeModal(String uuidStr) {
    System.out.println("Виклик модального: " + uuidStr);
    try {
      UUID uuid = UUID.fromString(uuidStr);
      Bicycle bike = bicycleService.getById(uuid);
      if (bike == null)
        return;

      Stage modal = new Stage();
      modal.initModality(Modality.APPLICATION_MODAL);
      modal.setTitle("Інформація про велосипед");

      VBox root = new VBox(15);
      root.setPadding(new Insets(20));
      root.setStyle(
          "-fx-background-color: linear-gradient(to bottom right, rgb(245, 247, 250), #c3cfe2);"
              + "-fx-background-radius: 20;"
              + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0.2, 0, 5);");

      Label modelLabel = new Label("Модель: " + bike.getModel());
      modelLabel.getStyleClass().add("label-title");

      Label priceLabel = new Label("Ціна: " + bike.getPricePerHour() + " грн/год");
      priceLabel.getStyleClass().add("label-success");

      Button rentButton = new Button("Орендувати");
      rentButton.getStyleClass().add("button-primary");

      rentButton.setOnAction(e -> {
        rentBike(bike.getId());
        modal.close();
      });

      root.getChildren().addAll(modelLabel, priceLabel, rentButton);

      Scene scene = new Scene(root);
      modal.setScene(scene);
      modal.showAndWait();
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
}