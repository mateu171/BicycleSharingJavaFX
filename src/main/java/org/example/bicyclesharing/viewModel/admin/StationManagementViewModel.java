package org.example.bicyclesharing.viewModel.admin;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class StationManagementViewModel extends BaseViewModel {

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
    load();
  }

  public ObservableList<Station> getStations() {
    return stations;
  }

  public void load() {
    stations.setAll(stationService.getAll());
    updateCount();
  }

  public void applyFilters() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    stations.setAll(stationService.findByFilters(search));
    updateCount();
  }

  public void delete(Station station) {
    if (station == null) return;
    stationService.deleteById(station.getId());
    applyFilters();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("admin.stations.count") + ": " + stations.size()
    );
  }
}