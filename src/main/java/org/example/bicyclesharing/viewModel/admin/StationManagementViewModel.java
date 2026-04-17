package org.example.bicyclesharing.viewModel.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;

public class StationManagementViewModel extends AsyncViewModel {

  private final StationService stationService;
  private final ObservableList<Station> stations = FXCollections.observableArrayList();

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("admin.stations.title");
  public final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("admin.stations.search");
  public final StringProperty addStationButtonText =
      LocalizationManager.getStringProperty("admin.stations.add");
  public final StringProperty countText = new SimpleStringProperty("");

  public final StringProperty searchText = new SimpleStringProperty("");

  public StationManagementViewModel(User currentUser, StationService stationService) {
    super(currentUser);
    this.stationService = stationService;
  }

  public ObservableList<Station> getStations() {
    return stations;
  }

  public void loadStationsAsync() {

    runAsync(stationService::getAll,
        result ->
        {
          stations.setAll(result);
          updateCount();
        });
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    runAsync(
        () -> stationService.findByFilters(search),
        result ->
        {
          stations.setAll(result);
          updateCount();
        }
    );
  }

  public void delete(Station station) {
    if (station == null) return;
    stationService.deleteStation(station);
    refreshAsync();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("admin.stations.count") + ": " + stations.size()
    );
  }

  public void refreshAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();

    if (search.isBlank()) {
      loadStationsAsync();
    } else {
      applyFiltersAsync();
    }
  }
}