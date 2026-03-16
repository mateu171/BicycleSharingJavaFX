package org.example.bicyclesharing.controller.view;

import java.util.List;
import java.util.UUID;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.AppConfig;

public class MapController extends BaseController {

  @FXML
  private WebView webView;
  private BicycleService bicycleService;
  private RentalService rentalService = AppConfig.rentalService();
  private User currentUser;
  private JSObject window;

  @FXML
  public void initialize() {
   bicycleService = AppConfig.bicycleService();
    WebEngine engine = webView.getEngine();

    var url = getClass().getResource("/org/example/bicyclesharing/map.html");
    engine.load(url.toExternalForm());

    engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {

      if (newState == Worker.State.SUCCEEDED) {
        System.out.println("WebView loaded!");
        this.window = (JSObject) engine.executeScript("window");
        this.window.setMember("javaApp", this);
        Platform.runLater(() -> loadBicycles());
      }
    });

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

  public void rentBike(String bicycleId) {
    Platform.runLater(() -> {
      System.out.println("Rent bike: " + bicycleId);

      UUID bikeUUID = UUID.fromString(bicycleId);
      UUID userId = currentUser.getId();

      Rental rental = new Rental(userId, bikeUUID);

      rentalService.add(rental);

      System.out.println("Rental created!");
    });
  }
  @Override
  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }
}