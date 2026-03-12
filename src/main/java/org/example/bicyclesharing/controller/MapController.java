package org.example.bicyclesharing.controller;

import java.util.List;
import java.util.Locale;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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

        loadBicycles();

      }

    });

  }

  public void rentBike(int id) {

    System.out.println("Велосипед орендовано: " + id);

  }

  private void loadBicycles() {

    List<Bicycle> bicycles = bicycleService.getAll();

    StringBuilder js = new StringBuilder();

    for (Bicycle bike : bicycles) {

      js.append(String.format(
          Locale.US,
          "addBike(%f,%f,\"%s\",%f);",
          bike.getLatitude(),
          bike.getLongitude(),
          bike.getModel(),
          bike.getPricePerHour()
      ));

    }
    webView.getEngine().executeScript(js.toString());

  }

}