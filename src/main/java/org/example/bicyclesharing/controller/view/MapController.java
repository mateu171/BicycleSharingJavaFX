package org.example.bicyclesharing.controller.view;

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
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.MapJSBuilder;
import org.example.bicyclesharing.viewModel.MapViewModel;

public class MapController extends BaseController {
  @FXML
  private WebView webView;
  @FXML
  private ListView<Bicycle> bikeList;
  @FXML
  private Label title;

  private JSObject window;
  private MapViewModel viewModel;

  @FXML
  public void initialize() {
    WebEngine engine = webView.getEngine();

    var url = getClass().getResource("/org/example/bicyclesharing/map.html");
    engine.load(url.toExternalForm());

    engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
      if (newState == Worker.State.SUCCEEDED) {
        window = (JSObject) engine.executeScript("window");
        window.setMember("javaApp", this);

        setupList();
        loadBicycles();
        webView.getEngine().executeScript("setTimeout(() => map.invalidateSize(), 300)");
      }
    });
  }

  private void setupList() {
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

        Label title = new Label(bicycle.getModel());
        title.getStyleClass().add("rental-title");

        Label price = new Label(bicycle.getPricePerMinute() + " " + viewModel.labelPrice.getValue());
        price.getStyleClass().add("rental-cost");

        Label timerLabel = new Label("00:00");
        timerLabel.getStyleClass().add("timer-label");
        timerLabel.textProperty().bind(viewModel.getRentalDurationProperty(bicycle));

        Button rentBtn = new Button();
        rentBtn.getStyleClass().add("button-primary");

        Rental activeRental = viewModel.getActiveRental(bicycle);

        if (activeRental != null) {
          rentBtn.setText(viewModel.finishButtonText.getValue());
          viewModel.startRentalTimer(bicycle, activeRental);
        } else {
          rentBtn.setText(viewModel.rentButtonText.getValue());
        }

        rentBtn.setOnAction(e -> {
          if (rentBtn.getText().equals(viewModel.rentButtonText.getValue())) {
            Rental rental = viewModel.rentBike(bicycle);
            rentBtn.setText(viewModel.finishButtonText.getValue());
            viewModel.startRentalTimer(bicycle, rental);
          } else {
            viewModel.finishRental(bicycle);
            rentBtn.setText(viewModel.rentButtonText.getValue());
            viewModel.stopRentalTimer(bicycle);
          }
        });

        card.getChildren().addAll(title, price, timerLabel, rentBtn);
        setGraphic(card);
      }
    });

    bikeList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, bike) -> {
      if (bike != null) {
        webView.getEngine().executeScript(
            "focusBike('" + bike.getId() + "');"
        );
      }
    });
  }

  private void loadBicycles() {
    bikeList.setItems(viewModel.bicycles);
    webView.getEngine().executeScript(MapJSBuilder.buildAddBikesScript(viewModel.bicycles));
  }

  public void selectBike(String bikeId) {
    Platform.runLater(() -> {
      Bicycle bike = viewModel.getBicycleById(bikeId);
      if (bike != null) {
        bikeList.getSelectionModel().select(bike);
        bikeList.scrollTo(bike);
      }
    });
  }

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new MapViewModel(currentUser);
    title.textProperty().bind(viewModel.titleText);
  }
}