package org.example.bicyclesharing.controller.view.mechanic;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.mechanic.MechanicServiceViewModel;

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

  @FXML private TableView<Bicycle> bicyclesTable;
  @FXML private TableColumn<Bicycle, String> modelColumn;
  @FXML private TableColumn<Bicycle, String> priceColumn;
  @FXML private TableColumn<Bicycle, String> stateColumn;

  private MechanicServiceViewModel viewModel;
  private FilteredList<Bicycle> filtered;
  private SortedList<Bicycle> sorted;

  @FXML
  public void initialize() {
    stateFilterCombo.getItems().addAll(
        LocalizationManager.getStringByKey("mechanic.filter.all"),
        LocalizationManager.getStringByKey("state.available"),
        LocalizationManager.getStringByKey("state.rented"),
        LocalizationManager.getStringByKey("state.needs.inspection"),
        LocalizationManager.getStringByKey("state.on.maintenance"),
        LocalizationManager.getStringByKey("state.unavailable")
    );
    stateFilterCombo.getSelectionModel().selectFirst();

    sortCombo.getItems().addAll(
        LocalizationManager.getStringByKey("mechanic.sort.bike"),
        LocalizationManager.getStringByKey("mechanic.sort.price"),
        LocalizationManager.getStringByKey("mechanic.sort.state")
    );
    sortCombo.getSelectionModel().selectFirst();

    modelColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getModel()));
    priceColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(String.valueOf(cell.getValue().getPricePerMinute())));
    stateColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(viewModel == null ? "" : viewModel.getStateText(cell.getValue())));

    inspectButton.disableProperty().bind(
        Bindings.isNull(bicyclesTable.getSelectionModel().selectedItemProperty())
    );
    maintenanceButton.disableProperty().bind(
        Bindings.isNull(bicyclesTable.getSelectionModel().selectedItemProperty())
    );
    availableButton.disableProperty().bind(
        Bindings.isNull(bicyclesTable.getSelectionModel().selectedItemProperty())
    );
    unavailableButton.disableProperty().bind(
        Bindings.isNull(bicyclesTable.getSelectionModel().selectedItemProperty())
    );
  }

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new MechanicServiceViewModel(currentUser, AppConfig.bicycleService());
    bindViewModel();
    setupFiltering();
    viewModel.loadBicyclesAsync();
  }

  private void bindViewModel() {
    titleLabel.textProperty().bind(viewModel.titleText);
    countLabel.textProperty().bind(viewModel.countText);
    modelColumn.textProperty().bind(viewModel.modelColumnText);
    priceColumn.textProperty().bind(viewModel.priceColumnText);
    stateColumn.textProperty().bind(viewModel.stateColumnText);
    inspectButton.textProperty().bind(viewModel.inspectButtonText);
    availableButton.textProperty().bind(viewModel.availableButtonText);
    maintenanceButton.textProperty().bind(viewModel.maintenanceButtonText);
    unavailableButton.textProperty().bind(viewModel.unavailableButtonText);
    searchLabel.textProperty().bind(viewModel.searchLabelText);
    sortLabel.textProperty().bind(viewModel.sortLabelText);
    statusLabel.textProperty().bind(viewModel.statusLabelText);
    searchField.promptTextProperty().bind(viewModel.searchPromText);

    filtered = new FilteredList<>(viewModel.getBicycles(), bicycle -> true);
    sorted = new SortedList<>(filtered);

    bicyclesTable.setItems(sorted);
    sorted.setComparator(viewModel.getComparator(sortCombo.getValue()));
  }

  private void setupFiltering() {
    Runnable refilter = () -> {
      if (viewModel == null) {
        return;
      }

      filtered.setPredicate(bicycle ->
          viewModel.matchesSearch(bicycle, searchField.getText())
              && viewModel.matchesState(bicycle, stateFilterCombo.getValue())
      );
      viewModel.updateCount();
    };

    searchField.textProperty().addListener((obs, oldV, newV) -> refilter.run());
    stateFilterCombo.valueProperty().addListener((obs, oldV, newV) -> refilter.run());

    sortCombo.valueProperty().addListener((obs, oldV, newV) -> {
      if (viewModel != null) {
        sorted.setComparator(viewModel.getComparator(newV));
      }
    });
  }

  @FXML
  private void onNeedsInspection() {
    viewModel.setState(
        bicyclesTable.getSelectionModel().getSelectedItem(),
        StateBicycle.NEEDS_INSPECTION
    );
    bicyclesTable.refresh();
  }

  @FXML
  private void onMaintenance() {
    viewModel.setState(
        bicyclesTable.getSelectionModel().getSelectedItem(),
        StateBicycle.ON_MAINTENANCE
    );
    bicyclesTable.refresh();
  }

  @FXML
  private void onAvailable() {
    viewModel.setState(
        bicyclesTable.getSelectionModel().getSelectedItem(),
        StateBicycle.AVAILABLE
    );
    bicyclesTable.refresh();
  }

  @FXML
  private void onUnavailable() {
    viewModel.setState(
        bicyclesTable.getSelectionModel().getSelectedItem(),
        StateBicycle.UNAVAILABLE
    );
    bicyclesTable.refresh();
  }
}