package org.example.bicyclesharing.viewModel.mechanic;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;
import org.example.bicyclesharing.viewModel.mechanic.item.MechanicServiceItemViewModel;

public class MechanicServiceViewModel extends AsyncViewModel {

  private final BicycleService bicycleService;

  private final ObservableList<MechanicServiceItemViewModel> bicycles =
      FXCollections.observableArrayList();

  private final FilteredList<MechanicServiceItemViewModel> filteredBicycles =
      new FilteredList<>(bicycles);

  private final SortedList<MechanicServiceItemViewModel> sortedBicycles =
      new SortedList<>(filteredBicycles);

  private final ObservableList<String> stateFilters =
      FXCollections.observableArrayList();

  private final ObservableList<String> sortOptions =
      FXCollections.observableArrayList();

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("mechanic.service.title");

  private final StringProperty modelColumnText =
      LocalizationManager.getStringProperty("mechanic.column.model");

  private final StringProperty priceColumnText =
      LocalizationManager.getStringProperty("mechanic.column.price");

  private final StringProperty stateColumnText =
      LocalizationManager.getStringProperty("mechanic.column.state");

  private final StringProperty inspectButtonText =
      LocalizationManager.getStringProperty("mechanic.button.inspect");

  private final StringProperty maintenanceButtonText =
      LocalizationManager.getStringProperty("mechanic.button.maintenance");

  private final StringProperty availableButtonText =
      LocalizationManager.getStringProperty("mechanic.button.available");

  private final StringProperty unavailableButtonText =
      LocalizationManager.getStringProperty("mechanic.button.unavailable");

  private final StringProperty searchLabelText =
      LocalizationManager.getStringProperty("mechanic.search");

  private final StringProperty statusLabelText =
      LocalizationManager.getStringProperty("mechanic.service.filter.state");

  private final StringProperty sortLabelText =
      LocalizationManager.getStringProperty("mechanic.sort");

  private final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("mechanic.search.prompt");

  private final StringProperty countText =
      new SimpleStringProperty("");

  private final StringProperty searchText =
      new SimpleStringProperty("");

  private final StringProperty selectedStateFilter =
      new SimpleStringProperty();

  private final StringProperty selectedSort =
      new SimpleStringProperty();

  private final ObjectProperty<MechanicServiceItemViewModel> selectedBicycle =
      new SimpleObjectProperty<>();

  private final BooleanProperty actionButtonsDisabled =
      new SimpleBooleanProperty(true);

  public MechanicServiceViewModel(
      User currentUser,
      BicycleService bicycleService
  ) {
    super(currentUser);
    this.bicycleService = bicycleService;

    initializeFilters();
    initializeSortOptions();
    setupFiltering();
    setupSorting();
    setupSelection();
  }

  public void initialize() {
    loadBicyclesAsync();
  }

  private void initializeFilters() {
    stateFilters.setAll(
        LocalizationManager.getStringByKey("mechanic.filter.all"),
        LocalizationManager.getStringByKey("state.available"),
        LocalizationManager.getStringByKey("state.rented"),
        LocalizationManager.getStringByKey("state.needs.inspection"),
        LocalizationManager.getStringByKey("state.on.maintenance"),
        LocalizationManager.getStringByKey("state.unavailable")
    );

    selectedStateFilter.set(LocalizationManager.getStringByKey("mechanic.filter.all"));
  }

  private void initializeSortOptions() {
    sortOptions.setAll(
        LocalizationManager.getStringByKey("mechanic.sort.bike"),
        LocalizationManager.getStringByKey("mechanic.sort.price"),
        LocalizationManager.getStringByKey("mechanic.sort.state")
    );

    selectedSort.set(LocalizationManager.getStringByKey("mechanic.sort.bike"));
  }

  private void setupFiltering() {
    searchText.addListener((obs, oldValue, newValue) -> applyFilters());
    selectedStateFilter.addListener((obs, oldValue, newValue) -> applyFilters());
  }

  private void setupSorting() {
    selectedSort.addListener((obs, oldValue, newValue) ->
        sortedBicycles.setComparator(resolveComparator(newValue))
    );

    sortedBicycles.setComparator(resolveComparator(selectedSort.get()));
  }

  private void setupSelection() {
    selectedBicycle.addListener((obs, oldValue, newValue) ->
        actionButtonsDisabled.set(newValue == null)
    );
  }

  public void loadBicyclesAsync() {
    runAsync(
        bicycleService::getAll,
        this::setBicycles
    );
  }

  private void setBicycles(List<Bicycle> result) {
    bicycles.setAll(
        result.stream()
            .map(this::toItemViewModel)
            .toList()
    );

    updateCount();
  }

  private MechanicServiceItemViewModel toItemViewModel(Bicycle bicycle) {
    return new MechanicServiceItemViewModel(
        bicycle,
        safe(bicycle.getModel()),
        String.valueOf(bicycle.getPricePerMinute()),
        getStateText(bicycle)
    );
  }

  private void applyFilters() {
    String search = searchText.get() == null
        ? ""
        : searchText.get().toLowerCase(Locale.ROOT).trim();

    String stateFilter = selectedStateFilter.get();
    String allFilter = LocalizationManager.getStringByKey("mechanic.filter.all");

    filteredBicycles.setPredicate(item -> {
      boolean matchesSearch =
          search.isBlank()
              || safe(item.modelTextProperty().get()).contains(search)
              || safe(item.stateTextProperty().get()).contains(search);

      boolean matchesState =
          stateFilter == null
              || stateFilter.equals(allFilter)
              || item.stateTextProperty().get().equals(stateFilter);

      return matchesSearch && matchesState;
    });

    updateCount();
  }

  private Comparator<MechanicServiceItemViewModel> resolveComparator(String sortValue) {
    String price = LocalizationManager.getStringByKey("mechanic.sort.price");
    String state = LocalizationManager.getStringByKey("mechanic.sort.state");

    if (price.equals(sortValue)) {
      return Comparator.comparingDouble(item ->
          item.getBicycle().getPricePerMinute()
      );
    }

    if (state.equals(sortValue)) {
      return Comparator.comparing(
          item -> item.stateTextProperty().get(),
          String.CASE_INSENSITIVE_ORDER
      );
    }

    return Comparator.comparing(
        item -> item.modelTextProperty().get(),
        String.CASE_INSENSITIVE_ORDER
    );
  }

  public void setSelectedState(StateBicycle state) {
    MechanicServiceItemViewModel item = selectedBicycle.get();

    if (item == null || state == null) {
      return;
    }

    Bicycle bicycle = item.getBicycle();
    bicycle.setState(state);
    bicycleService.update(bicycle);

    loadBicyclesAsync();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("mechanic.count")
            + ": "
            + filteredBicycles.size()
    );
  }

  private String getStateText(Bicycle bicycle) {
    if (bicycle == null || bicycle.getState() == null) {
      return "-";
    }

    return LocalizationManager.getStringByKey(bicycle.getState().getKey());
  }

  private String safe(String value) {
    return value == null ? "" : value.toLowerCase(Locale.ROOT);
  }

  public SortedList<MechanicServiceItemViewModel> getSortedBicycles() {
    return sortedBicycles;
  }

  public ObservableList<String> getStateFilters() {
    return stateFilters;
  }

  public ObservableList<String> getSortOptions() {
    return sortOptions;
  }

  public ObjectProperty<MechanicServiceItemViewModel> selectedBicycleProperty() {
    return selectedBicycle;
  }

  public StringProperty selectedStateFilterProperty() {
    return selectedStateFilter;
  }

  public StringProperty selectedSortProperty() {
    return selectedSort;
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }

  public BooleanProperty actionButtonsDisabledProperty() {
    return actionButtonsDisabled;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty modelColumnTextProperty() {
    return modelColumnText;
  }

  public StringProperty priceColumnTextProperty() {
    return priceColumnText;
  }

  public StringProperty stateColumnTextProperty() {
    return stateColumnText;
  }

  public StringProperty inspectButtonTextProperty() {
    return inspectButtonText;
  }

  public StringProperty maintenanceButtonTextProperty() {
    return maintenanceButtonText;
  }

  public StringProperty availableButtonTextProperty() {
    return availableButtonText;
  }

  public StringProperty unavailableButtonTextProperty() {
    return unavailableButtonText;
  }

  public StringProperty searchLabelTextProperty() {
    return searchLabelText;
  }

  public StringProperty statusLabelTextProperty() {
    return statusLabelText;
  }

  public StringProperty sortLabelTextProperty() {
    return sortLabelText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
  }

  public StringProperty countTextProperty() {
    return countText;
  }
}