package org.example.bicyclesharing.viewModel.mechanic;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.MaintenanceRecordService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;
import org.example.bicyclesharing.viewModel.mechanic.item.MaintenanceRecordItemViewModel;

public class MaintenanceHistoryViewModel extends AsyncViewModel {

  private static final String ALL_FILTER_KEY = "mechanic.filter.all";

  private final MaintenanceRecordService maintenanceRecordService;
  private final BicycleService bicycleService;

  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private final ObservableList<MaintenanceRecordItemViewModel> records =
      FXCollections.observableArrayList();

  private final FilteredList<MaintenanceRecordItemViewModel> filteredRecords =
      new FilteredList<>(records, item -> true);

  private final ObservableList<String> typeFilters =
      FXCollections.observableArrayList();

  private final StringProperty titleLabelText =
      LocalizationManager.getStringProperty("maintenance.history.title");

  private final StringProperty typeProblemLabelText =
      LocalizationManager.getStringProperty("mechanic.history.type");

  private final StringProperty searchLabelText =
      LocalizationManager.getStringProperty("mechanic.search");

  private final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("mechanic.search.prompt");

  private final StringProperty bikeColumnText =
      LocalizationManager.getStringProperty("mechanic.column.bike");

  private final StringProperty typeColumnText =
      LocalizationManager.getStringProperty("mechanic.column.problem");

  private final StringProperty descriptionColumnText =
      LocalizationManager.getStringProperty("mechanic.column.comment");

  private final StringProperty resultColumnText =
      LocalizationManager.getStringProperty("mechanic.column.result");

  private final StringProperty dateColumnText =
      LocalizationManager.getStringProperty("mechanic.column.date");

  private final StringProperty conclusionColumnText =
      LocalizationManager.getStringProperty("mechanic.column.сonclusion");

  private final StringProperty searchText =
      new SimpleStringProperty("");

  private final StringProperty selectedTypeFilter =
      new SimpleStringProperty();

  public MaintenanceHistoryViewModel(
      User currentUser,
      MaintenanceRecordService maintenanceRecordService,
      BicycleService bicycleService
  ) {
    super(currentUser);
    this.maintenanceRecordService = maintenanceRecordService;
    this.bicycleService = bicycleService;

    initializeTypeFilters();
    setupFiltering();
  }

  public void initialize() {
    loadAsync();
  }

  private void initializeTypeFilters() {
    typeFilters.setAll(LocalizationManager.getStringByKey(ALL_FILTER_KEY));

    for (MaintenanceType type : MaintenanceType.values()) {
      typeFilters.add(LocalizationManager.getStringByKey(type.getKey()));
    }

    selectedTypeFilter.set(LocalizationManager.getStringByKey(ALL_FILTER_KEY));
  }

  public void loadAsync() {
    runAsync(
        maintenanceRecordService::getAll,
        this::setRecords
    );
  }

  private void setRecords(List<MaintenanceRecord> result) {
    records.setAll(
        result.stream()
            .map(this::toItemViewModel)
            .toList()
    );

    applyFilters();
  }

  private MaintenanceRecordItemViewModel toItemViewModel(MaintenanceRecord record) {
    return new MaintenanceRecordItemViewModel(
        record,
        getBikeText(record),
        getTypeText(record),
        safe(record.getDescription()),
        safe(record.getResult()),
        getDateText(record),
        getActionText(record)
    );
  }

  private String getBikeText(MaintenanceRecord record) {
    if (record == null || record.getBicycleId() == null) {
      return "-";
    }

    Bicycle bicycle = bicycleService.getById(record.getBicycleId()).orElse(null);
    return bicycle == null ? "-" : safe(bicycle.getModel());
  }

  private String getTypeText(MaintenanceRecord record) {
    if (record == null || record.getType() == null) {
      return "-";
    }

    return LocalizationManager.getStringByKey(record.getType().getKey());
  }

  private String getActionText(MaintenanceRecord record) {
    if (record == null || record.getAction() == null) {
      return "-";
    }

    return LocalizationManager.getStringByKey(record.getAction().getKey());
  }

  private String getDateText(MaintenanceRecord record) {
    if (record == null || record.getCreatedAt() == null) {
      return "-";
    }

    return record.getCreatedAt().format(formatter);
  }

  private String safe(String value) {
    return value == null || value.isBlank() ? "-" : value;
  }

  private void setupFiltering() {
    searchText.addListener((obs, oldValue, newValue) -> applyFilters());
    selectedTypeFilter.addListener((obs, oldValue, newValue) -> applyFilters());
  }

  private void applyFilters() {
    String search = searchText.get() == null
        ? ""
        : searchText.get().toLowerCase(Locale.ROOT).trim();

    String typeFilter = selectedTypeFilter.get();

    filteredRecords.setPredicate(item -> {
      boolean matchesSearch =
          search.isBlank()
              || item.bikeTextProperty().get().toLowerCase(Locale.ROOT).contains(search);

      boolean matchesType =
          typeFilter == null
              || typeFilter.equals(LocalizationManager.getStringByKey(ALL_FILTER_KEY))
              || item.typeTextProperty().get().equals(typeFilter);

      return matchesSearch && matchesType;
    });
  }

  public FilteredList<MaintenanceRecordItemViewModel> getFilteredRecords() {
    return filteredRecords;
  }

  public ObservableList<String> getTypeFilters() {
    return typeFilters;
  }

  public StringProperty selectedTypeFilterProperty() {
    return selectedTypeFilter;
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }

  public StringProperty titleLabelTextProperty() {
    return titleLabelText;
  }

  public StringProperty typeProblemLabelTextProperty() {
    return typeProblemLabelText;
  }

  public StringProperty searchLabelTextProperty() {
    return searchLabelText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
  }

  public StringProperty bikeColumnTextProperty() {
    return bikeColumnText;
  }

  public StringProperty typeColumnTextProperty() {
    return typeColumnText;
  }

  public StringProperty descriptionColumnTextProperty() {
    return descriptionColumnText;
  }

  public StringProperty resultColumnTextProperty() {
    return resultColumnText;
  }

  public StringProperty dateColumnTextProperty() {
    return dateColumnText;
  }

  public StringProperty conclusionColumnTextProperty() {
    return conclusionColumnText;
  }
}