package org.example.bicyclesharing.viewModel.admin;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class BicyclesManagementViewModel extends BaseViewModel {

  private final BicycleService bicycleService;
  private final StationService stationService;
  private final ObservableList<Bicycle> bicycles = FXCollections.observableArrayList();

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("admin.bicycles.title");
  public final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("admin.bicycles.search");
  public final StringProperty addBikeButtonText =
      LocalizationManager.getStringProperty("admin.bicycles.add");
  public final StringProperty countText = new SimpleStringProperty("");

  public final StringProperty searchText = new SimpleStringProperty("");
  public final StringProperty selectedStateFilter = new SimpleStringProperty("ALL");

  public BicyclesManagementViewModel(User currentUser, BicycleService bicycleService,StationService stationService) {
    super(currentUser);
    this.bicycleService = bicycleService;
    this.stationService = stationService;
    loadBicycles();
  }

  public ObservableList<Bicycle> getBicycles() {
    return bicycles;
  }

  public void loadBicycles() {
    bicycles.setAll(bicycleService.getAll());
    updateCount();
  }

  public void applyFilters() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    StateBicycle stateFilter = resolveSelectedState();

    bicycles.setAll(bicycleService.getByFilters(search, stateFilter));
    updateCount();
  }

  public void deleteBicycle(Bicycle bicycle) {
    if (bicycle == null) return;

    if (bicycle.getStationId() != null) {
      Station station = stationService.getById(bicycle.getStationId());
      if (station != null) {
        station.removeBicycleId(bicycle.getId());
        stationService.update(station);
      }
    }

    bicycleService.deleteById(bicycle.getId());
    applyFilters();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("admin.bicycles.count") + ": " + bicycles.size()
    );
  }

  private StateBicycle resolveSelectedState() {
    String stateFilterText = selectedStateFilter.get();

    if (stateFilterText == null || stateFilterText.equals("ALL")) {
      return null;
    }

    for (StateBicycle state : StateBicycle.values()) {
      String localizedState = LocalizationManager.getStringByKey(state.getKey());
      if (localizedState.equals(stateFilterText)) {
        return state;
      }
    }

    return null;
  }
}