package org.example.bicyclesharing.viewModel.admin;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;
import org.example.bicyclesharing.viewModel.admin.item.StationItemViewModel;

public class StationManagementViewModel extends AsyncViewModel {

  private final StationService stationService;

  private final ObservableList<StationItemViewModel> stations =
      FXCollections.observableArrayList();

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("admin.stations.title");

  private final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("admin.stations.search");

  private final StringProperty addStationButtonText =
      LocalizationManager.getStringProperty("admin.stations.add");

  private final StringProperty countText =
      new SimpleStringProperty("");

  private final StringProperty searchText =
      new SimpleStringProperty("");

  public StationManagementViewModel(
      User currentUser,
      StationService stationService
  ) {
    super(currentUser);
    this.stationService = stationService;
  }

  public void initialize() {
    loadStationsAsync();
  }

  public void loadStationsAsync() {
    runAsync(
        stationService::getAll,
        this::setStations
    );
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();

    runAsync(
        () -> stationService.findByFilters(search),
        this::setStations
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

  public void delete(StationItemViewModel item) {
    if (item == null) {
      return;
    }

    stationService.deleteStation(item.getStation().getId());
    refreshAsync();
  }

  private void setStations(List<Station> result) {
    stations.setAll(
        result.stream()
            .map(StationItemViewModel::new)
            .toList()
    );

    updateCount();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("admin.stations.count")
            + ": "
            + stations.size()
    );
  }

  public ObservableList<StationItemViewModel> getStations() {
    return stations;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
  }

  public StringProperty addStationButtonTextProperty() {
    return addStationButtonText;
  }

  public StringProperty countTextProperty() {
    return countText;
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }
}