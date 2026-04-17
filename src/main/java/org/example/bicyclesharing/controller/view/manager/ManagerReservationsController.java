package org.example.bicyclesharing.controller.view.manager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.manager.modalController.AddEditReservationController;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.manager.ManagerReservationsViewModel;

public class ManagerReservationsController extends BaseController {

  @FXML private Button addReservationButton;
  @FXML private Label titleLabel;
  @FXML private TextField searchField;
  @FXML private ComboBox<String> statusFilterCombo;
  @FXML private Label countLabel;
  @FXML private ListView<Reservation> reservationsListView;

  private ManagerReservationsViewModel viewModel;
  private User currentUser;

  @Override
  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;

    viewModel = new ManagerReservationsViewModel(
        currentUser,
        AppConfig.reservationService(),
        AppConfig.rentalService(),
        AppConfig.customerService(),
        AppConfig.bicycleService()
    );

    binds();
    setupFilters();
    setupList();
    viewModel.loadReservationsAsync();
  }

  private void binds() {
    titleLabel.textProperty().bind(viewModel.titleText);
    searchField.promptTextProperty().bind(viewModel.searchPromptText);
    countLabel.textProperty().bind(viewModel.countText);
    searchField.textProperty().bindBidirectional(viewModel.searchText);
    reservationsListView.setItems(viewModel.getReservations());

    addReservationButton.textProperty().bind(viewModel.addButtonText);

    statusFilterCombo.getItems().setAll(
        LocalizationManager.getStringByKey("manager.reservations.filter.all"),
        LocalizationManager.getStringByKey("reservation.status.new"),
        LocalizationManager.getStringByKey("reservation.status.issued"),
        LocalizationManager.getStringByKey("reservation.status.cancelled")
    );
    statusFilterCombo.getSelectionModel().selectFirst();
  }

  private void setupFilters() {
    searchField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.applyFiltersAsync());

    statusFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
      viewModel.statusFilterText.set(newVal);
      viewModel.applyFiltersAsync();
    });
  }

  private void setupList() {
    reservationsListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Reservation reservation, boolean empty) {
        super.updateItem(reservation, empty);

        if (empty || reservation == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        VBox card = new VBox(8);
        card.getStyleClass().add("user-card");

        Label customerLabel = new Label(viewModel.getCustomerName(reservation));
        customerLabel.getStyleClass().add("user-card-title");

        Label bicycleLabel = new Label(
            LocalizationManager.getStringByKey("manager.reservations.card.bicycle")
                + ": " + viewModel.getBicycleModel(reservation)
        );
        bicycleLabel.getStyleClass().add("user-card-subtitle");

        Label periodLabel = new Label(
            LocalizationManager.getStringByKey("manager.reservations.card.period")
                + ": " + viewModel.getStartText(reservation)
                + " - " + viewModel.getEndText(reservation)
        );
        periodLabel.getStyleClass().add("user-card-subtitle");

        Label documentLabel = new Label(
            LocalizationManager.getStringByKey("manager.reservations.card.document")
                + ": " + viewModel.getDocumentText(reservation)
        );
        documentLabel.getStyleClass().add("user-card-subtitle");

        Label depositLabel = new Label(
            LocalizationManager.getStringByKey("manager.reservations.card.deposit")
                + ": " + viewModel.getDepositText(reservation)
        );
        depositLabel.getStyleClass().add("user-card-subtitle");

        Label statusLabel = new Label(viewModel.getStatusText(reservation));
        statusLabel.getStyleClass().add("user-card-role");

        Button issueCardButton = new Button();
        issueCardButton.textProperty().bind(viewModel.issueButtonText);
        issueCardButton.getStyleClass().add("button-edit");
        issueCardButton.setDisable(!viewModel.canIssue(reservation));
        issueCardButton.setOnAction(e -> viewModel.issueReservation(reservation));

        Button editCardButton = new Button();
        editCardButton.textProperty().bind(viewModel.editButtonText);
        editCardButton.getStyleClass().add("button-edit");
        editCardButton.setDisable(!viewModel.canIssue(reservation));
        editCardButton.setOnAction(e -> openReservationDialog(reservation));

        Button cancelCardButton = new Button();
        cancelCardButton.textProperty().bind(viewModel.cancelButtonText);
        cancelCardButton.getStyleClass().add("button-danger");
        cancelCardButton.setDisable(!viewModel.canCancel(reservation));
        cancelCardButton.setOnAction(e -> viewModel.cancelReservation(reservation));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10, issueCardButton, editCardButton, cancelCardButton);
        HBox bottomRow = new HBox(10, statusLabel, spacer, actions);

        card.getChildren().addAll(
            customerLabel,
            bicycleLabel,
            periodLabel,
            documentLabel,
            depositLabel,
            bottomRow
        );

        setGraphic(card);
      }
    });
  }

  @FXML
  private void onAddReservation() {
    openReservationDialog(null);
  }

  private void openReservationDialog(Reservation reservation) {
    try {
      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/manager/modalView/AddEditReservationView.fxml",
          (AddEditReservationController controller) -> controller.initData(
              currentUser,
              reservation,
              viewModel::refreshAsync
          )
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}