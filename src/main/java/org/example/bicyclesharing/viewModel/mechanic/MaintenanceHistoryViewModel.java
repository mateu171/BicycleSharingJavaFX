package org.example.bicyclesharing.viewModel.mechanic;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.services.MaintenanceRecordService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;

public class MaintenanceHistoryViewModel {

  public final StringProperty titleLabelText = LocalizationManager.getStringProperty("maintenance.history.title");
  public final StringProperty typeProblemLabelText = LocalizationManager.getStringProperty("mechanic.history.type");
  public final StringProperty searchLabelText = LocalizationManager.getStringProperty("mechanic.search");
  public final StringProperty searchPromText = LocalizationManager.getStringProperty("mechanic.search.prompt");
  public final StringProperty bikeColumnText = LocalizationManager.getStringProperty("mechanic.column.bike");
  public final StringProperty typeColumnText = LocalizationManager.getStringProperty("mechanic.column.problem");
  public final StringProperty descriptionColumnText = LocalizationManager.getStringProperty("mechanic.column.comment");
  public final StringProperty resultColumnText = LocalizationManager.getStringProperty("mechanic.column.result");
  public final StringProperty dateColumnText = LocalizationManager.getStringProperty("mechanic.column.date");
  public final StringProperty conclusionColumnText= LocalizationManager.getStringProperty("mechanic.column.сonclusion");
  private final MaintenanceRecordService service =
      AppConfig.maintenanceRecordService();

  private final ObservableList<MaintenanceRecord> records =
      FXCollections.observableArrayList();

  public MaintenanceHistoryViewModel() {
    load();
  }

  public ObservableList<MaintenanceRecord> getRecords() {
    return records;
  }

  public void load() {
    records.setAll(service.getAll());
  }
}