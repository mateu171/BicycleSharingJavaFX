package org.example.bicyclesharing.controller.view.mechanic;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.mechanic.MechanicServiceViewModel;
import org.example.bicyclesharing.viewModel.mechanic.item.MechanicServiceItemViewModel;

public class MechanicServiceController extends BaseController {

  @FXML private Label searchLabel;
  @FXML private Label statusLabel;
  @FXML private Label sortLabel;
  @FXML private Label titleLabel;
  @FXML private Label countLabel;

  @FXML private TextField searchField;
  @FXML private ComboBox<String> stateFilterCombo;
  @FXML private ComboBox<String> sortCombo;

  @FXML private Button inspectButton;
  @FXML private Button maintenanceButton;
  @FXML private Button availableButton;
  @FXML private Button unavailableButton;

  @FXML private TableView<MechanicServiceItemViewModel> bicyclesTable;
  @FXML private TableColumn<MechanicServiceItemViewModel, String> modelColumn;
  @FXML private TableColumn<MechanicServiceItemViewModel, String> priceColumn;
  @FXML private TableColumn<MechanicServiceItemViewModel, String> stateColumn;

  private MechanicServiceViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new MechanicServiceViewModel(
        currentUser,
        AppConfig.bicycleService()
    );

    bind();
    setupTable();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    countLabel.textProperty().bind(viewModel.countTextProperty());

    modelColumn.textProperty().bind(viewModel.modelColumnTextProperty());
    priceColumn.textProperty().bind(viewModel.priceColumnTextProperty());
    stateColumn.textProperty().bind(viewModel.stateColumnTextProperty());

    inspectButton.textProperty().bind(viewModel.inspectButtonTextProperty());
    availableButton.textProperty().bind(viewModel.availableButtonTextProperty());
    maintenanceButton.textProperty().bind(viewModel.maintenanceButtonTextProperty());
    unavailableButton.textProperty().bind(viewModel.unavailableButtonTextProperty());

    searchLabel.textProperty().bind(viewModel.searchLabelTextProperty());
    sortLabel.textProperty().bind(viewModel.sortLabelTextProperty());
    statusLabel.textProperty().bind(viewModel.statusLabelTextProperty());
    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
    stateFilterCombo.valueProperty().bindBidirectional(viewModel.selectedStateFilterProperty());
    sortCombo.valueProperty().bindBidirectional(viewModel.selectedSortProperty());

    stateFilterCombo.setItems(viewModel.getStateFilters());
    sortCombo.setItems(viewModel.getSortOptions());

    bicyclesTable.getSelectionModel()
        .selectedItemProperty()
        .addListener((obs, oldValue, newValue) ->
            viewModel.selectedBicycleProperty().set(newValue)
        );

    inspectButton.disableProperty().bind(viewModel.actionButtonsDisabledProperty());
    maintenanceButton.disableProperty().bind(viewModel.actionButtonsDisabledProperty());
    availableButton.disableProperty().bind(viewModel.actionButtonsDisabledProperty());
    unavailableButton.disableProperty().bind(viewModel.actionButtonsDisabledProperty());
  }

  private void setupTable() {
    modelColumn.setCellValueFactory(cell ->
        cell.getValue().modelTextProperty());

    priceColumn.setCellValueFactory(cell ->
        cell.getValue().priceTextProperty());

    stateColumn.setCellValueFactory(cell ->
        cell.getValue().stateTextProperty());

    bicyclesTable.setItems(viewModel.getSortedBicycles());
  }

  @FXML
  private void onNeedsInspection() {
    viewModel.setSelectedState(StateBicycle.NEEDS_INSPECTION);
    bicyclesTable.refresh();
  }

  @FXML
  private void onMaintenance() {
    viewModel.setSelectedState(StateBicycle.ON_MAINTENANCE);
    bicyclesTable.refresh();
  }

  @FXML
  private void onAvailable() {
    viewModel.setSelectedState(StateBicycle.AVAILABLE);
    bicyclesTable.refresh();
  }

  @FXML
  private void onUnavailable() {
    viewModel.setSelectedState(StateBicycle.UNAVAILABLE);
    bicyclesTable.refresh();
  }
}