package org.example.bicyclesharing.controller.view.mechanic;

import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.MaintenanceRecordService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.mechanic.MaintenanceHistoryViewModel;

public class MaintenanceHistoryController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private TextField searchField;
  @FXML private ComboBox<String> typeFilterCombo;

  @FXML private TableView<MaintenanceRecord> historyTable;
  @FXML private TableColumn<MaintenanceRecord, String> bikeColumn;
  @FXML private TableColumn<MaintenanceRecord, String> typeColumn;
  @FXML private TableColumn<MaintenanceRecord, String> descriptionColumn;
  @FXML private TableColumn<MaintenanceRecord, String> resultColumn;
  @FXML private TableColumn<MaintenanceRecord, String> dateColumn;


  private final BicycleService bicycleService = AppConfig.bicycleService();
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
  private MaintenanceHistoryViewModel viewModel;

  @FXML
  public void initialize() {
    viewModel = new MaintenanceHistoryViewModel();
    titleLabel.setText(LocalizationManager.getStringByKey("maintenance.history.title"));
    searchField.setPromptText(LocalizationManager.getStringByKey("mechanic.search.prompt"));

    bikeColumn.setText(LocalizationManager.getStringByKey("mechanic.column.bike"));
    typeColumn.setText(LocalizationManager.getStringByKey("maintenance.column.type"));
    descriptionColumn.setText(LocalizationManager.getStringByKey("maintenance.column.description"));
    resultColumn.setText(LocalizationManager.getStringByKey("maintenance.column.result"));
    dateColumn.setText(LocalizationManager.getStringByKey("mechanic.column.date"));

    typeFilterCombo.getItems().add(LocalizationManager.getStringByKey("mechanic.filter.all"));

    for(var type : MaintenanceType.values())
    {
      typeFilterCombo.getItems().add(LocalizationManager.getStringByKey(type.getKey()));
    }
    typeFilterCombo.getSelectionModel().selectFirst();

    bikeColumn.setCellValueFactory(cell ->
    {
      Bicycle bicycle = bicycleService.getById(cell.getValue().getBicycleId());
      return new SimpleStringProperty(bicycle.getModel());
    });

    typeColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(LocalizationManager.getStringByKey(cell.getValue().getType().getKey())));
    descriptionColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(cell.getValue().getDescription()));
    resultColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(cell.getValue().getResult()));
    dateColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(cell.getValue().getCreatedAt().format(formatter)));
    FilteredList<MaintenanceRecord> filtered = new FilteredList<>(viewModel.getRecords(), r -> true);
    Runnable refilter = () -> filtered.setPredicate(record -> {
      String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
      String typeFilter = typeFilterCombo.getValue();

      Bicycle bicycle = bicycleService.getById(record.getBicycleId());
      String bikeName = bicycle != null ? bicycle.getModel().toLowerCase() : "";
      String typeName = LocalizationManager.getStringByKey(record.getType().getKey());

      boolean matchesSearch = search.isBlank()
          || bikeName.contains(search)
          || record.getDescription().toLowerCase().contains(search)
          || record.getResult().toLowerCase().contains(search)
          || typeName.toLowerCase().contains(search);

      boolean matchesType = typeFilter == null
          || typeFilter.equals(LocalizationManager.getStringByKey("mechanic.filter.all"))
          || typeName.equals(typeFilter);

      return matchesSearch && matchesType;
    });

    searchField.textProperty().addListener((obs, oldV, newV) -> refilter.run());
    typeFilterCombo.valueProperty().addListener((obs, oldV, newV) -> refilter.run());

    historyTable.setItems(filtered);
  }

  @Override
  public void setCurrentUser(User currentUser) {
  }
}

