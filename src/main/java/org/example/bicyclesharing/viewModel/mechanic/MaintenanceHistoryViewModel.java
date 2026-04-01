package org.example.bicyclesharing.viewModel.mechanic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.services.MaintenanceRecordService;
import org.example.bicyclesharing.util.AppConfig;

public class MaintenanceHistoryViewModel {

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