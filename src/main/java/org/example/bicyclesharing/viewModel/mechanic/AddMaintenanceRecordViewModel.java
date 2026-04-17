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
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class AddMaintenanceRecordViewModel extends BaseViewModel {

  private final BicycleService bicycleService = AppConfig.bicycleService();
  private final MaintenanceRecordService maintenanceRecordService = AppConfig.maintenanceRecordService();

  private final ObservableList<Bicycle> bicycles = FXCollections.observableArrayList();
  private final FilteredList<Bicycle> filteredBicycles = new FilteredList<>(bicycles, bicycle -> true);

  public final StringProperty searchText = new SimpleStringProperty("");
  public final ObjectProperty<Bicycle> selectedBicycle = new SimpleObjectProperty<>();
  public final ObjectProperty<MaintenanceType> selectedType = new SimpleObjectProperty<>();
  public final StringProperty description = new SimpleStringProperty("");
  public final StringProperty result = new SimpleStringProperty("");
  public final ObjectProperty<MaintenanceAction> selectedAction = new SimpleObjectProperty<>();

  public final StringProperty actionErrorKey = new SimpleStringProperty("");
  public final StringProperty bicycleErrorKey = new SimpleStringProperty("");
  public final StringProperty typeErrorKey = new SimpleStringProperty("");
  public final StringProperty descriptionErrorKey = new SimpleStringProperty("");
  public final StringProperty resultErrorKey = new SimpleStringProperty("");
  public final StringProperty successMessageKey = new SimpleStringProperty("");

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("maintenance.add.title");
  public final StringProperty bikeSectionTitleText =
      LocalizationManager.getStringProperty("maintenance.section.bike");
  public final StringProperty recordSectionTitleText =
      LocalizationManager.getStringProperty("maintenance.section.record");
  public final StringProperty typeLabelText =
      LocalizationManager.getStringProperty("maintenance.type");
  public final StringProperty descriptionLabelText =
      LocalizationManager.getStringProperty("maintenance.description");
  public final StringProperty resultLabelText =
      LocalizationManager.getStringProperty("maintenance.result");
  public final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  public final StringProperty clearButtonText =
      LocalizationManager.getStringProperty("clear.button");
  public final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("maintenance.search.prompt");
  public final StringProperty modelColumnText =
      LocalizationManager.getStringProperty("mechanic.column.model");
  public final StringProperty stateColumnText =
      LocalizationManager.getStringProperty("mechanic.column.state");
  public final StringProperty actionText =
      LocalizationManager.getStringProperty("maintenance.action");

  public AddMaintenanceRecordViewModel(User currentUser) {
    super(currentUser);
    loadBicycles();
    setupFiltering();
  }

  private void loadBicycles() {
    bicycles.setAll(bicycleService.getAll());
  }

  private void setupFiltering() {
    searchText.addListener((obs, oldValue, newValue) -> {
      String search = newValue == null ? "" : newValue.toLowerCase(Locale.ROOT).trim();

      filteredBicycles.setPredicate(bicycle ->
          search.isBlank()
              || bicycle.getModel().toLowerCase(Locale.ROOT).contains(search)
      );
    });
  }

  public FilteredList<Bicycle> getFilteredBicycles() {
    return filteredBicycles;
  }

  public String getStateText(Bicycle bicycle) {
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

      Bicycle selectedBike = selectedBicycle.get();
      selectedBike.setState(
          selectedAction.get() == MaintenanceAction.RETURN_TO_AVAILABLE
              ? StateBicycle.AVAILABLE
              : StateBicycle.UNAVAILABLE
      );
      bicycleService.update(selectedBike);

      clearForm();
      loadBicycles();
      successMessageKey.set("maintenance.success");
      return true;

    } catch (CustomEntityValidationExeption e) {
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
      return false;
    }
  }

  public void clearErrors() {
    bicycleErrorKey.set("");
    typeErrorKey.set("");
    descriptionErrorKey.set("");
    resultErrorKey.set("");
    actionErrorKey.set("");
  }
}