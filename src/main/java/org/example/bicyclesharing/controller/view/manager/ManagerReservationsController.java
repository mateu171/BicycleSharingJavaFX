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
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.manager.ManagerReservationsViewModel;
import org.example.bicyclesharing.viewModel.manager.item.ReservationItemViewModel;

public class ManagerReservationsController extends BaseController {

  @FXML private Button addReservationButton;
  @FXML private Label titleLabel;
  @FXML private TextField searchField;
  @FXML private ComboBox<String> statusFilterCombo;
  @FXML private Label countLabel;
  @FXML private ListView<ReservationItemViewModel> reservationsListView;

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

    bind();
    setupList();
    setupFilters();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());
    countLabel.textProperty().bind(viewModel.countTextProperty());
    addReservationButton.textProperty().bind(viewModel.addButtonTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
    statusFilterCombo.valueProperty().bindBidirectional(viewModel.statusFilterTextProperty());

    reservationsListView.setItems(viewModel.getReservations());
  }

  private void setupFilters() {
    statusFilterCombo.setItems(viewModel.getStatusFilters());
    statusFilterCombo.getSelectionModel().selectFirst();

    searchField.textProperty().addListener((obs, oldVal, newVal) ->
        viewModel.applyFiltersAsync()
    );

    statusFilterCombo.valueProperty().addListener((obs, oldVal, newVal) ->
        viewModel.applyFiltersAsync()
    );
  }

  private void setupList() {
    reservationsListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(ReservationItemViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        setGraphic(createCard(item));
      }
    });
  }

  private VBox createCard(ReservationItemViewModel item) {
    VBox card = new VBox(8);
    card.getStyleClass().add("user-card");

    Label customerLabel = new Label();
    customerLabel.getStyleClass().add("user-card-title");
    customerLabel.textProperty().bind(item.customerTextProperty());

    Label bicycleLabel = new Label();
    bicycleLabel.getStyleClass().add("user-card-subtitle");
    bicycleLabel.textProperty().bind(item.bicycleTextProperty());

    Label periodLabel = new Label();
    periodLabel.getStyleClass().add("user-card-subtitle");
    periodLabel.textProperty().bind(item.periodTextProperty());

    Label documentLabel = new Label();
    documentLabel.getStyleClass().add("user-card-subtitle");
    documentLabel.textProperty().bind(item.documentTextProperty());

    Label depositLabel = new Label();
    depositLabel.getStyleClass().add("user-card-subtitle");
    depositLabel.textProperty().bind(item.depositTextProperty());

    Label statusLabel = new Label();
    statusLabel.getStyleClass().add("user-card-role");
    statusLabel.textProperty().bind(item.statusTextProperty());

    Button issueButton = new Button();
    issueButton.textProperty().bind(viewModel.issueButtonTextProperty());
    issueButton.getStyleClass().add("button-edit");
    issueButton.disableProperty().bind(item.canIssueProperty().not());
    issueButton.setOnAction(e -> viewModel.issueReservation(item));

    Button editButton = new Button();
    editButton.textProperty().bind(viewModel.editButtonTextProperty());
    editButton.getStyleClass().add("button-edit");
    editButton.disableProperty().bind(item.canIssueProperty().not());
    editButton.setOnAction(e -> openReservationDialog(item));

    Button cancelButton = new Button();
    cancelButton.textProperty().bind(viewModel.cancelButtonTextProperty());
    cancelButton.getStyleClass().add("button-danger");
    cancelButton.disableProperty().bind(item.canCancelProperty().not());
    cancelButton.setOnAction(e -> viewModel.cancelReservation(item));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox actions = new HBox(10, issueButton, editButton, cancelButton);
    HBox bottomRow = new HBox(10, statusLabel, spacer, actions);

    card.getChildren().addAll(
        customerLabel,
        bicycleLabel,
        periodLabel,
        documentLabel,
        depositLabel,
        bottomRow
    );

    return card;
  }

  @FXML
  private void onAddReservation() {
    openReservationDialog(null);
  }

  private void openReservationDialog(ReservationItemViewModel item) {
    try {
      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/manager/modalView/AddEditReservationView.fxml",
          (AddEditReservationController controller) ->
              controller.initData(
                  currentUser,
                  item == null ? null : item.getReservation(),
                  viewModel::refreshAsync
              )
      );
    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.operation.failed"));
    }
  }
}