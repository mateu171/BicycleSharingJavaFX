package org.example.bicyclesharing.viewModel.admin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import org.example.bicyclesharing.viewModel.AsyncViewModel;

public class BicyclesManagementViewModel extends AsyncViewModel {

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

  public BicyclesManagementViewModel(
      User currentUser,
      BicycleService bicycleService,
      StationService stationService
  ) {
    super(currentUser);
    this.bicycleService = bicycleService;
    this.stationService = stationService;
  }

  public ObservableList<Bicycle> getBicycles() {
    return bicycles;
  }

  public void loadBicyclesAsync() {
    runAsync(
        bicycleService::getAll,
        result -> {
          bicycles.setAll(result);
          updateCount();
        }
    );
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    StateBicycle stateFilter = resolveSelectedState();

    runAsync(
        () -> bicycleService.getByFilters(search, stateFilter),
        result -> {
          bicycles.setAll(result);
          updateCount();
        }
    );
  }

  public void refreshAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    String stateFilterText = selectedStateFilter.get();

    boolean noFilters = search.isBlank()
        && (stateFilterText == null || stateFilterText.equals("ALL"));

    if (noFilters) {
      loadBicyclesAsync();
    } else {
      applyFiltersAsync();
    }
  }

  public void deleteBicycle(Bicycle bicycle) {
    if (bicycle == null) {
      return;
    }

    bicycleService.validateCanDelete(bicycle);

    if (bicycle.getStationId() != null) {
      Station station = stationService.getById(bicycle.getStationId());
      if (station != null) {
        station.removeBicycleId(bicycle.getId());
        stationService.update(station);
      }
    }

    bicycleService.deleteById(bicycle.getId());
    refreshAsync();
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