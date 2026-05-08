package org.example.bicyclesharing.viewModel.admin;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.viewModel.admin.item.BicycleItemViewModel;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.StationService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;

public class BicyclesManagementViewModel extends AsyncViewModel {

  private static final String ALL_FILTER = "ALL";

  private final BicycleService bicycleService;
  private final StationService stationService;

  private final ObservableList<BicycleItemViewModel> bicycles = FXCollections.observableArrayList();
  private final ObservableList<String> stateFilters = FXCollections.observableArrayList();


  public final StringProperty titleText = LocalizationManager.getStringProperty("admin.bicycles.title");
  public final StringProperty searchPromptText = LocalizationManager.getStringProperty("admin.bicycles.search");
  public final StringProperty addBikeButtonText = LocalizationManager.getStringProperty("admin.bicycles.add");
  public final StringProperty countText = new SimpleStringProperty("");

  public final StringProperty searchText = new SimpleStringProperty("");
  public final StringProperty selectedStateFilter = new SimpleStringProperty(ALL_FILTER);

  public BicyclesManagementViewModel(
      User currentUser,
      BicycleService bicycleService,
      StationService stationService
  ) {
    super(currentUser);
    this.bicycleService = bicycleService;
    this.stationService = stationService;

    initializeFilters();
  }

  private void initializeFilters() {
    stateFilters.setAll(
        ALL_FILTER,
        LocalizationManager.getStringByKey(StateBicycle.AVAILABLE.getKey()),
        LocalizationManager.getStringByKey(StateBicycle.RENTED.getKey()),
        LocalizationManager.getStringByKey(StateBicycle.UNAVAILABLE.getKey()),
        LocalizationManager.getStringByKey(StateBicycle.NEEDS_INSPECTION.getKey()),
        LocalizationManager.getStringByKey(StateBicycle.ON_MAINTENANCE.getKey())
    );

    selectedStateFilter.set(ALL_FILTER);
  }
  public void initialize()
  {
    loadBicyclesAsync();
  }

  public void loadBicyclesAsync() {
    runAsync(
        bicycleService::getAll,
        this::setBicycles
    );
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    StateBicycle stateFilter = resolveSelectedState();

    runAsync(
        () -> bicycleService.getByFilters(search, stateFilter),
        this::setBicycles
    );
  }

  public void validateCanEdit(BicycleItemViewModel item)
  {
    if(item == null)
      return;

    bicycleService.validateCanEdit(item.getBicycle());
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

  public void deleteBicycle(BicycleItemViewModel item) {
    if (item == null) {
      return;
    }
    Bicycle bicycle = item.getBicycle();
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

  private void setBicycles(List<Bicycle> result)
  {
    bicycles.setAll(result.stream().map(BicycleItemViewModel::new).toList());
    updateCount();
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

  public ObservableList<BicycleItemViewModel> getBicycles() {
    return bicycles;
  }

  public ObservableList<String> getStateFilters() {
    return stateFilters;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
  }

  public StringProperty addBikeButtonTextProperty() {
    return addBikeButtonText;
  }

  public StringProperty countTextProperty() {
    return countText;
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }

  public StringProperty selectedStateFilterProperty() {
    return selectedStateFilter;
  }
}