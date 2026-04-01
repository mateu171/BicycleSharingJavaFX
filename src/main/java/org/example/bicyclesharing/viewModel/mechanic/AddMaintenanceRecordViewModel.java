package org.example.bicyclesharing.viewModel.mechanic;

import java.util.Locale;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.MaintenanceRecordService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddMaintenanceRecordViewModel {

  private final MechanicServiceViewModel bicycleViewModel = new MechanicServiceViewModel();
  private final MaintenanceRecordService maintenanceRecordService = AppConfig.maintenanceRecordService();

  public final StringProperty searchText = new SimpleStringProperty("");
  public final ObjectProperty<Bicycle> selectedBicycle = new SimpleObjectProperty<>();
  public final ObjectProperty<MaintenanceType> selectedType = new SimpleObjectProperty<>();
  public final StringProperty description = new SimpleStringProperty("");
  public final StringProperty result = new SimpleStringProperty("");
  public final BooleanProperty returnedToAvailable = new SimpleBooleanProperty(false);
  public final BooleanProperty writtenOff = new SimpleBooleanProperty(false);

  public final StringProperty bicycleErrorKey = new SimpleStringProperty("");
  public final StringProperty typeErrorKey = new SimpleStringProperty("");
  public final StringProperty descriptionErrorKey = new SimpleStringProperty("");
  public final StringProperty resultErrorKey = new SimpleStringProperty("");
  public final StringProperty flagsErrorKey = new SimpleStringProperty("");
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
  public final StringProperty returnedText =
      LocalizationManager.getStringProperty("maintenance.returned");
  public final StringProperty writeOffText =
      LocalizationManager.getStringProperty("maintenance.write_off");
  public final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("maintenance.search.prompt");
  public final StringProperty modelColumnText =
      LocalizationManager.getStringProperty("mechanic.column.model");
  public final StringProperty stateColumnText =
      LocalizationManager.getStringProperty("mechanic.column.state");

  private User currentUser;

  public void setCurrentUser(User currentUser) {
    this.currentUser = currentUser;
  }

  public FilteredList<Bicycle> getFilteredBicycles() {
    FilteredList<Bicycle> filtered = new FilteredList<>(bicycleViewModel.getBicycles(), bicycle -> true);

    searchText.addListener((obs, oldValue, newValue) -> {
      String search = newValue == null ? "" : newValue.toLowerCase(Locale.ROOT).trim();

      filtered.setPredicate(bicycle ->
          search.isBlank()
              || bicycle.getModel().toLowerCase(Locale.ROOT).contains(search)
              || LocalizationManager.getStringByKey(bicycle.getState().getKey())
              .toLowerCase(Locale.ROOT)
              .contains(search)
      );
    });

    return filtered;
  }

  public String getStateText(Bicycle bicycle) {
    return LocalizationManager.getStringByKey(bicycle.getState().getKey());
  }

  public void clearForm() {
    selectedBicycle.set(null);
    selectedType.set(null);
    description.set("");
    result.set("");
    returnedToAvailable.set(false);
    writtenOff.set(false);
    clearErrors();
    successMessageKey.set("");
  }

  public boolean save() {
    clearErrors();
    successMessageKey.set("");

    if (selectedBicycle.get() == null) {
      bicycleErrorKey.set("maintenance.bicycle.empty");
      return false;
    }

    if (currentUser == null) {
      flagsErrorKey.set("maintenance.mechanic.empty");
      return false;
    }

    try {
      MaintenanceRecord record = new MaintenanceRecord(
          selectedBicycle.get().getId(),
          currentUser.getId(),
          null,
          selectedType.get(),
          description.get(),
          result.get(),
          returnedToAvailable.get(),
          writtenOff.get()
      );

      maintenanceRecordService.add(record);
      clearForm();
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
          case "statusFlags", "mechanicId", "createdAt" -> flagsErrorKey.set(key);
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
    flagsErrorKey.set("");
  }
}