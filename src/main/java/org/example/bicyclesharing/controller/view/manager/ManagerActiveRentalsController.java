package org.example.bicyclesharing.controller.view.manager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.manager.modalController.FinishRentalDialogController;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.manager.ManagerActiveRentalsViewModel;

public class ManagerActiveRentalsController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private TextField searchField;
  @FXML private Label countLabel;
  @FXML private ListView<Rental> rentalsListView;

  private ManagerActiveRentalsViewModel viewModel;
  private User currentUser;

  @Override
  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
    viewModel = new ManagerActiveRentalsViewModel(
        currentUser,
        AppConfig.rentalService(),
        AppConfig.customerService(),
        AppConfig.bicycleService()
    );
    bind();
    setupFilters();
    setupList();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    searchField.promptTextProperty().bind(viewModel.searchPromptText);
    countLabel.textProperty().bind(viewModel.countText);
    searchField.textProperty().bindBidirectional(viewModel.searchText);
    rentalsListView.setItems(viewModel.getRentals());
  }

  private void setupFilters() {
    searchField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.applyFilters());
  }

  private void setupList() {
    rentalsListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Rental rental, boolean empty) {
        super.updateItem(rental, empty);

        if (empty || rental == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        VBox card = new VBox(8);
        card.getStyleClass().add("user-card");

        Label customerLabel = new Label(viewModel.getCustomerName(rental));
        customerLabel.getStyleClass().add("user-card-title");

        Label bicycleLabel = new Label(
            LocalizationManager.getStringByKey("manager.rentals.active.card.bicycle")
                + ": " + viewModel.getBicycleModel(rental)
        );
        bicycleLabel.getStyleClass().add("user-card-subtitle");

        Label startLabel = new Label(
            LocalizationManager.getStringByKey("manager.rentals.active.card.start")
                + ": " + viewModel.getStartText(rental)
        );
        startLabel.getStyleClass().add("user-card-subtitle");

        Button finishCardButton = new Button();
        finishCardButton.textProperty().bind(viewModel.finishButtonText);
        finishCardButton.getStyleClass().add("button-primary");
        finishCardButton.setOnAction(e -> openFinishDialog(rental));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10, spacer, finishCardButton);

        card.getChildren().addAll(customerLabel, bicycleLabel, startLabel, actions);
        setGraphic(card);
      }
    });
  }

  private void openFinishDialog(Rental rental) {
    try {
      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/manager/modalView/FinishRentalDialog.fxml",
          (FinishRentalDialogController controller) -> controller.initData(currentUser, rental, () -> {
            viewModel.loadRentals();
            viewModel.applyFilters();
          })
      );

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}