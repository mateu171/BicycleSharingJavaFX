package org.example.bicyclesharing.viewModel.mechanic;

import java.util.Locale;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.MaintenanceAction;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.MaintenanceRecordService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class AddMaintenanceRecordViewModel extends BaseViewModel {

  private final BicycleService bicycleService;
  private final MaintenanceRecordService maintenanceRecordService;

  private final ObservableList<Bicycle> bicycles = FXCollections.observableArrayList();
  private final FilteredList<Bicycle> filteredBicycles =
      new FilteredList<>(bicycles, bicycle -> true);

  private final StringProperty searchText = new SimpleStringProperty("");
  private final ObjectProperty<Bicycle> selectedBicycle = new SimpleObjectProperty<>();
  private final ObjectProperty<MaintenanceType> selectedType = new SimpleObjectProperty<>();
  private final StringProperty description = new SimpleStringProperty("");
  private final StringProperty result = new SimpleStringProperty("");
  private final ObjectProperty<MaintenanceAction> selectedAction = new SimpleObjectProperty<>();

  private final StringProperty actionErrorKey = new SimpleStringProperty("");
  private final StringProperty bicycleErrorKey = new SimpleStringProperty("");
  private final StringProperty typeErrorKey = new SimpleStringProperty("");
  private final StringProperty descriptionErrorKey = new SimpleStringProperty("");
  private final StringProperty resultErrorKey = new SimpleStringProperty("");
  private final StringProperty successMessageKey = new SimpleStringProperty("");

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("maintenance.add.title");
  private final StringProperty bikeSectionTitleText =
      LocalizationManager.getStringProperty("maintenance.section.bike");
  private final StringProperty recordSectionTitleText =
      LocalizationManager.getStringProperty("maintenance.section.record");
  private final StringProperty typeLabelText =
      LocalizationManager.getStringProperty("maintenance.type");
  private final StringProperty descriptionLabelText =
      LocalizationManager.getStringProperty("maintenance.description");
  private final StringProperty resultLabelText =
      LocalizationManager.getStringProperty("maintenance.result");
  private final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  private final StringProperty clearButtonText =
      LocalizationManager.getStringProperty("clear.button");
  private final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("maintenance.search.prompt");
  private final StringProperty modelColumnText =
      LocalizationManager.getStringProperty("mechanic.column.model");
  private final StringProperty stateColumnText =
      LocalizationManager.getStringProperty("mechanic.column.state");
  private final StringProperty actionText =
      LocalizationManager.getStringProperty("maintenance.action");

  public AddMaintenanceRecordViewModel(
      User currentUser,
      BicycleService bicycleService,
      MaintenanceRecordService maintenanceRecordService
  ) {
    super(currentUser);
    this.bicycleService = bicycleService;
    this.maintenanceRecordService = maintenanceRecordService;

    setupFiltering();
    loadBicycles();
  }

  private void loadBicycles() {
    bicycles.setAll(bicycleService.getAll());
  }

  private void setupFiltering() {
    searchText.addListener((obs, oldValue, newValue) -> {
      String search = newValue == null
          ? ""
          : newValue.toLowerCase(Locale.ROOT).trim();

      filteredBicycles.setPredicate(bicycle ->
          search.isBlank()
              || bicycle.getModel().toLowerCase(Locale.ROOT).contains(search)
      );
    });
  }

  public void selectBicycle(Bicycle bicycle) {
    selectedBicycle.set(bicycle);
    bicycleErrorKey.set("");
  }

  public void clearTypeError() {
    typeErrorKey.set("");
  }

  public void clearDescriptionError() {
    descriptionErrorKey.set("");
  }

  public void clearResultError() {
    resultErrorKey.set("");
  }

  public void clearActionError() {
    actionErrorKey.set("");
  }

  public String getStateText(Bicycle bicycle) {
    if (bicycle == null || bicycle.getState() == null) {
      return "-";
    }

    return LocalizationManager.getStringByKey(bicycle.getState().getKey());
  }

  public void clearForm() {
    selectedBicycle.set(null);
    selectedType.set(null);
    description.set("");
    result.set("");
    selectedAction.set(null);
    clearErrors();
    successMessageKey.set("");
  }

  public boolean save() {
    clearErrors();
    successMessageKey.set("");

    try {
      MaintenanceRecord record = new MaintenanceRecord(
          selectedBicycle.get() == null ? null : selectedBicycle.get().getId(),
          currentUser.getId(),
          selectedType.get(),
          description.get(),
          result.get(),
          selectedAction.get()
      );

      maintenanceRecordService.add(record);
      updateBicycleState();

      clearForm();
      loadBicycles();
      successMessageKey.set("maintenance.success");

      return true;

    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;
    }
  }

  private void updateBicycleState() {
    Bicycle bicycle = selectedBicycle.get();

    if (bicycle == null || selectedAction.get() == null) {
      return;
    }

    bicycle.setState(
        selectedAction.get() == MaintenanceAction.RETURN_TO_AVAILABLE
            ? StateBicycle.AVAILABLE
            : StateBicycle.UNAVAILABLE
    );

    bicycleService.update(bicycle);
  }

  private void applyValidationErrors(CustomEntityValidationExeption e) {
    e.getErrors().forEach((field, keys) -> {
      if (keys == null || keys.isEmpty()) {
        return;
      }

      String key = keys.get(0);

      switch (field) {
        case "bicycleId" -> bicycleErrorKey.set(key);
        case "type" -> typeErrorKey.set(key);
        case "description" -> descriptionErrorKey.set(key);
        case "result" -> resultErrorKey.set(key);
        case "action" -> actionErrorKey.set(key);
      }
    });
  }

  private void clearErrors() {
    bicycleErrorKey.set("");
    typeErrorKey.set("");
    descriptionErrorKey.set("");
    resultErrorKey.set("");
    actionErrorKey.set("");
  }

  public FilteredList<Bicycle> getFilteredBicycles() {
    return filteredBicycles;
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }

  public ObjectProperty<Bicycle> selectedBicycleProperty() {
    return selectedBicycle;
  }

  public ObjectProperty<MaintenanceType> selectedTypeProperty() {
    return selectedType;
  }

  public StringProperty descriptionProperty() {
    return description;
  }

  public StringProperty resultProperty() {
    return result;
  }

  public ObjectProperty<MaintenanceAction> selectedActionProperty() {
    return selectedAction;
  }

  public StringProperty actionErrorKeyProperty() {
    return actionErrorKey;
  }

  public StringProperty bicycleErrorKeyProperty() {
    return bicycleErrorKey;
  }

  public StringProperty typeErrorKeyProperty() {
    return typeErrorKey;
  }

  public StringProperty descriptionErrorKeyProperty() {
    return descriptionErrorKey;
  }

  public StringProperty resultErrorKeyProperty() {
    return resultErrorKey;
  }

  public StringProperty successMessageKeyProperty() {
    return successMessageKey;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty bikeSectionTitleTextProperty() {
    return bikeSectionTitleText;
  }

  public StringProperty recordSectionTitleTextProperty() {
    return recordSectionTitleText;
  }

  public StringProperty typeLabelTextProperty() {
    return typeLabelText;
  }

  public StringProperty descriptionLabelTextProperty() {
    return descriptionLabelText;
  }

  public StringProperty resultLabelTextProperty() {
    return resultLabelText;
  }

  public StringProperty saveButtonTextProperty() {
    return saveButtonText;
  }

  public StringProperty clearButtonTextProperty() {
    return clearButtonText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
  }

  public StringProperty modelColumnTextProperty() {
    return modelColumnText;
  }

  public StringProperty stateColumnTextProperty() {
    return stateColumnText;
  }

  public StringProperty actionTextProperty() {
    return actionText;
  }
}