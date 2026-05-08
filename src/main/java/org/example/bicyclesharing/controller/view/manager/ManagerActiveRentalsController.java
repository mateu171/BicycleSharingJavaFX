package org.example.bicyclesharing.controller.view.manager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.manager.modalController.FinishRentalDialogController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.manager.ManagerActiveRentalsViewModel;
import org.example.bicyclesharing.viewModel.manager.item.ActiveRentalItemViewModel;

public class ManagerActiveRentalsController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private TextField searchField;
  @FXML private Label countLabel;
  @FXML private ListView<ActiveRentalItemViewModel> rentalsListView;

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
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());
    countLabel.textProperty().bind(viewModel.countTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
    rentalsListView.setItems(viewModel.getRentals());
  }

  private void setupFilters() {
    searchField.textProperty().addListener((obs, oldVal, newVal) ->
        viewModel.applyFiltersAsync()
    );
  }

  private void setupList() {
    rentalsListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(ActiveRentalItemViewModel item, boolean empty) {
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

  private VBox createCard(ActiveRentalItemViewModel item) {
    VBox card = new VBox(8);
    card.getStyleClass().add("user-card");

    Label customerLabel = new Label();
    customerLabel.getStyleClass().add("user-card-title");
    customerLabel.textProperty().bind(item.customerTextProperty());

    Label bicycleLabel = new Label();
    bicycleLabel.getStyleClass().add("user-card-subtitle");
    bicycleLabel.textProperty().bind(item.bicycleTextProperty());

    Label startLabel = new Label();
    startLabel.getStyleClass().add("user-card-subtitle");
    startLabel.textProperty().bind(item.startTextProperty());

    Button finishCardButton = new Button();
    finishCardButton.textProperty().bind(viewModel.finishButtonTextProperty());
    finishCardButton.getStyleClass().add("button-primary");
    finishCardButton.setOnAction(e -> openFinishDialog(item));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox actions = new HBox(10, spacer, finishCardButton);

    card.getChildren().addAll(customerLabel, bicycleLabel, startLabel, actions);
    return card;
  }

  private void openFinishDialog(ActiveRentalItemViewModel item) {
    try {
      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/manager/modalView/FinishRentalDialog.fxml",
          (FinishRentalDialogController controller) ->
              controller.initData(
                  item.getRental(),
                  viewModel::refreshAsync
              )
      );

    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.operation.failed"));
    }
  }
}