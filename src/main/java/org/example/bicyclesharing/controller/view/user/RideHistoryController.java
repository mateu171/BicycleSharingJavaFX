package org.example.bicyclesharing.controller.view.user;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.user.RideHistoryViewModel;

import java.time.format.DateTimeFormatter;

public class RideHistoryController extends BaseController {

  @FXML
  private ListView<Rental> rentalList;
  @FXML
  private Label title;

  private final BicycleService bicycleService = AppConfig.bicycleService();
  private RideHistoryViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    this.viewModel = new RideHistoryViewModel(AppConfig.rentalService(),currentUser);
    rentalList.setItems(viewModel.getRentals());
    setupCellFactory();
  }

  private void setupCellFactory() {
    title.textProperty().bind(viewModel.titleText);
    rentalList.getStyleClass().add("rental-list");
    rentalList.setSelectionModel(null);

    rentalList.setCellFactory(list -> new ListCell<>() {

      @Override
      protected void updateItem(Rental rental, boolean empty) {
        super.updateItem(rental, empty);

        if (empty || rental == null) {
          setGraphic(null);
          return;
        }

        VBox card = new VBox(5);
        card.getStyleClass().add("rental-card");

        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        Bicycle bicycle = bicycleService.getById(rental.getBicycleId());
        String bicycleName = bicycle != null ? bicycle.getModel() : "Невідомий велосипед";

        Label bikeLabel = new Label(
            LocalizationManager.getStringByKey("history.bike") + bicycleName
        );
        bikeLabel.getStyleClass().add("rental-title");

        Label startLabel = new Label(LocalizationManager.getStringByKey("history.start") + rental.getStart().format(formatter));

        Label endLabel = new Label(
            rental.getEnd() != null
                ? LocalizationManager.getStringByKey("history.end") + rental.getEnd().format(formatter)
                : LocalizationManager.getStringByKey("history.active")
        );

        Label costLabel = new Label(
            LocalizationManager.getStringByKey("history.total") + String.format("%.2f грн", rental.getTotalCost())
        );
        costLabel.getStyleClass().add("rental-cost");

        card.getChildren().addAll(bikeLabel, startLabel, endLabel, costLabel);

        setGraphic(card);
      }
    });
  }
}