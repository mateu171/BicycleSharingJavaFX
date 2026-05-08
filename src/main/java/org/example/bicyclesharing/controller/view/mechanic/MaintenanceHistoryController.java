package org.example.bicyclesharing.controller.view.mechanic;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.mechanic.MaintenanceHistoryViewModel;
import org.example.bicyclesharing.viewModel.mechanic.item.MaintenanceRecordItemViewModel;

public class MaintenanceHistoryController extends BaseController {

  @FXML private Label searchLabel;
  @FXML private Label typeProblemLabel;
  @FXML private Label titleLabel;
  @FXML private TextField searchField;
  @FXML private ComboBox<String> typeFilterCombo;

  @FXML private TableView<MaintenanceRecordItemViewModel> historyTable;
  @FXML private TableColumn<MaintenanceRecordItemViewModel, String> bikeColumn;
  @FXML private TableColumn<MaintenanceRecordItemViewModel, String> typeColumn;
  @FXML private TableColumn<MaintenanceRecordItemViewModel, String> descriptionColumn;
  @FXML private TableColumn<MaintenanceRecordItemViewModel, String> resultColumn;
  @FXML private TableColumn<MaintenanceRecordItemViewModel, String> dateColumn;
  @FXML private TableColumn<MaintenanceRecordItemViewModel, String> conclusionColumn;

  private MaintenanceHistoryViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new MaintenanceHistoryViewModel(
        currentUser,
        AppConfig.maintenanceRecordService(),
        AppConfig.bicycleService()
    );

    bind();
    setupTable();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleLabelTextProperty());
    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());
    searchLabel.textProperty().bind(viewModel.searchLabelTextProperty());
    typeProblemLabel.textProperty().bind(viewModel.typeProblemLabelTextProperty());

    bikeColumn.textProperty().bind(viewModel.bikeColumnTextProperty());
    typeColumn.textProperty().bind(viewModel.typeColumnTextProperty());
    descriptionColumn.textProperty().bind(viewModel.descriptionColumnTextProperty());
    resultColumn.textProperty().bind(viewModel.resultColumnTextProperty());
    dateColumn.textProperty().bind(viewModel.dateColumnTextProperty());
    conclusionColumn.textProperty().bind(viewModel.conclusionColumnTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
    typeFilterCombo.valueProperty().bindBidirectional(viewModel.selectedTypeFilterProperty());

    typeFilterCombo.setItems(viewModel.getTypeFilters());
  }

  private void setupTable() {
    bikeColumn.setCellValueFactory(cell ->
        cell.getValue().bikeTextProperty());

    typeColumn.setCellValueFactory(cell ->
        cell.getValue().typeTextProperty());

    descriptionColumn.setCellValueFactory(cell ->
        cell.getValue().descriptionTextProperty());

    resultColumn.setCellValueFactory(cell ->
        cell.getValue().resultTextProperty());

    dateColumn.setCellValueFactory(cell ->
        cell.getValue().dateTextProperty());

    conclusionColumn.setCellValueFactory(cell ->
        cell.getValue().actionTextProperty());

    historyTable.setItems(viewModel.getFilteredRecords());
  }
}