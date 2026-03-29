package org.example.bicyclesharing.controller.view.user;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.viewModel.user.MapViewModel;

public class MapController extends BaseController {

  @FXML private WebView webView;
  @FXML private ListView<Bicycle> bikeList;
  @FXML private Label title;

  private JSObject window;
  private MapViewModel viewModel;
  private boolean mapLoaded = false;

  @FXML
  public void initialize() {
    WebEngine engine = webView.getEngine();

    var url = getClass().getResource("/org/example/bicyclesharing/map.html");
    engine.load(url.toExternalForm());

    engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
      if (newState == Worker.State.SUCCEEDED) {
        mapLoaded = true;

        try {
          window = (JSObject) engine.executeScript("window");
          window.setMember("javaApp", this);
          System.out.println("javaApp injected into JS");
        } catch (Exception e) {
          e.printStackTrace();
        }

        tryInitMapData();

        engine.executeScript("setTimeout(() => map.invalidateSize(), 300)");
      }
    });
  }

  private void tryInitMapData() {
    if (!mapLoaded || viewModel == null) {
      return;
    }

    setupList();
    clearMapMarkers();
    loadStationsToMap();
  }

  private void setupList() {
    bikeList.setItems(viewModel.getBicyclesForSelectedStation());

    bikeList.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Bicycle bicycle, boolean empty) {
        super.updateItem(bicycle, empty);

        if (empty || bicycle == null) {
          setGraphic(null);
          return;
        }

        VBox card = new VBox(5);
        card.getStyleClass().add("rental-card");

        Label bicycleTitle = new Label(bicycle.getModel());
        bicycleTitle.getStyleClass().add("rental-title");

        Label price = new Label(bicycle.getPricePerMinute() + " " + viewModel.labelPrice.get());
        price.getStyleClass().add("rental-cost");

        Label timerLabel = new Label();
        timerLabel.getStyleClass().add("timer-label");
        timerLabel.textProperty().bind(viewModel.getRentalDurationProperty(bicycle));

        Button rentBtn = new Button();
        rentBtn.getStyleClass().add("button-primary");

        Rental activeRental = viewModel.getActiveRental(bicycle);

        if (activeRental != null) {
          rentBtn.setText(viewModel.finishButtonText.get());
          viewModel.startRentalTimer(bicycle, activeRental);
        } else {
          rentBtn.setText(viewModel.rentButtonText.get());
        }

        rentBtn.setOnAction(e -> {
          if (rentBtn.getText().equals(viewModel.rentButtonText.get())) {
            Rental rental = viewModel.rentBike(bicycle);
            if (rental != null) {
              rentBtn.setText(viewModel.finishButtonText.get());
              viewModel.startRentalTimer(bicycle, rental);
            }
          } else {
            viewModel.finishRental(bicycle);
            rentBtn.setText(viewModel.rentButtonText.get());
            viewModel.stopRentalTimer(bicycle);
          }

          bikeList.refresh();
        });

        card.getChildren().addAll(bicycleTitle, price, timerLabel, rentBtn);
        setGraphic(card);
      }
    });
  }

  private void loadStationsToMap() {
    if (window == null || viewModel == null) {
      return;
    }

    for (Station station : viewModel.getStations()) {
      String safeName = escapeJs(station.getName());

      String script = String.format(
          "addStationMarker('%s', %s, %s, '%s')",
          station.getId(),
          station.getLatitude(),
          station.getLongitude(),
          safeName
      );

      webView.getEngine().executeScript(script);
    }
  }

  private void clearMapMarkers() {
    if (webView != null && mapLoaded) {
      webView.getEngine().executeScript("clearStationMarkers()");
    }
  }

  private String escapeJs(String text) {
    if (text == null) {
      return "";
    }
    return text
        .replace("\\", "\\\\")
        .replace("'", "\\'")
        .replace("\"", "\\\"");
  }

  public void selectStation(String stationId) {
    if (viewModel == null) {
      return;
    }

    Station station = viewModel.getStationById(stationId);
    if (station != null) {
      viewModel.selecStation(station);
    }
  }

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new MapViewModel(currentUser);
    title.textProperty().bind(viewModel.titleText);

    if (bikeList != null) {
      bikeList.setItems(viewModel.getBicyclesForSelectedStation());
    }

    tryInitMapData();
  }
}