package org.example.bicyclesharing.controller.view;

import java.util.List;
import java.util.UUID;
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
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.MapViewModel;

public class MapController extends BaseController {

  @FXML
  private WebView webView;
  @FXML
  private ListView<Bicycle> bikeList;
  @FXML
  private Label title;

  private BicycleService bicycleService = AppConfig.bicycleService();
  private RentalService rentalService = AppConfig.rentalService();
  private User currentUser;
  private JSObject window;
  private MapViewModel viewModel;

  @FXML
  public void initialize() {
    WebEngine engine = webView.getEngine();
    viewModel = new MapViewModel();
    title.textProperty().bind(viewModel.titleText);

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

        Button rentBtn = new Button(viewModel.rentButtonText.getValue());
        rentBtn.getStyleClass().add("button-primary");

        Rental activeRental = rentalService.getByUserId(currentUser.getId())
            .stream()
            .filter(r -> r.getBicycleId().equals(bicycle.getId()) && r.getEnd() == null)
            .findFirst()
            .orElse(null);

        if (activeRental != null) {
          rentBtn.setText(viewModel.finishButtonText.getValue());
        }

        rentBtn.setOnAction(e -> {
          if (rentBtn.getText().equals(viewModel.rentButtonText.getValue())) {
            Rental rental = new Rental(currentUser.getId(), bicycle.getId());
            rentalService.add(rental);
            rentBtn.setText(viewModel.finishButtonText.getValue());
            System.out.println("Rental started: " + bicycle.getId());
          } else {
            Rental rental = rentalService.getByUserId(currentUser.getId())
                .stream()
                .filter(r -> r.getBicycleId().equals(bicycle.getId()) && r.getEnd() == null)
                .findFirst()
                .orElse(null);

            if (rental != null) {
              rentalService.finishRental(rental);
              rentBtn.setText(viewModel.rentButtonText.getValue());
              System.out.println("Rental finished: " + bicycle.getId());
            }
          }
        });

        card.getChildren().addAll(title, price, rentBtn);
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
    List<Bicycle> bicycles = bicycleService.getAll();

    bikeList.getItems().clear();

    StringBuilder js = new StringBuilder();

    for (Bicycle bike : bicycles) {
      bikeList.getItems().add(bike);
      js.append(String.format(
          java.util.Locale.US,
          "addBike(%f,%f,'%s',%f,'%s');",
          bike.getLatitude(),
          bike.getLongitude(),
          bike.getModel().replace("'", "\\'"),
          bike.getPricePerMinute(),
          bike.getId().toString()
      ));
    }

    webView.getEngine().executeScript(js.toString());
  }

  public void selectBike(String bikeId)
  {
    Platform.runLater(() -> {
      for (Bicycle bike : bikeList.getItems()) {
        if (bike.getId().toString().equals(bikeId)) {
          bikeList.getSelectionModel().select(bike);
          bikeList.scrollTo(bike);
          break;
        }
      }
    });
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