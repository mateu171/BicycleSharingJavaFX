package org.example.bicyclesharing.viewModel.mechanic;

import java.util.Comparator;
import java.util.Locale;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;

public class MechanicServiceViewModel {

  private final BicycleService bicycleService = AppConfig.bicycleService();
  private final ObservableList<Bicycle> bicycles = FXCollections.observableArrayList();

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("mechanic.service.title");
  public final StringProperty countText = new SimpleStringProperty();

  public MechanicServiceViewModel() {
    loadBicycles();
  }

  public ObservableList<Bicycle> getBicycles() {
    return bicycles;
  }

  public void loadBicycles() {
    bicycles.setAll(bicycleService.getAll());
    updateCount();
  }

  public void updateCount() {
    countText.set(LocalizationManager.getStringByKey("mechanic.count") + ": " + bicycles.size());
  }

  public String getStateText(Bicycle bicycle) {
    return LocalizationManager.getStringByKey(bicycle.getState().getKey());
  }

  public boolean matchesSearch(Bicycle bicycle, String search) {
    if (search == null || search.isBlank()) {
      return true;
    }

    String normalized = search.toLowerCase(Locale.ROOT).trim();

    return safe(bicycle.getModel()).contains(normalized)
        || safe(getStateText(bicycle)).contains(normalized);
  }

  public boolean matchesState(Bicycle bicycle, String stateValue) {
    if (stateValue == null
        || stateValue.equals(LocalizationManager.getStringByKey("mechanic.filter.all"))) {
      return true;
    }

    return getStateText(bicycle).equals(stateValue);
  }

  public Comparator<Bicycle> getComparator(String sortValue) {
    String model = LocalizationManager.getStringByKey("mechanic.sort.bike");
    String price = LocalizationManager.getStringByKey("mechanic.sort.price");
    String state = LocalizationManager.getStringByKey("mechanic.sort.state");

    if (price.equals(sortValue)) {
      return Comparator.comparingDouble(Bicycle::getPricePerMinute);
    }

    if (state.equals(sortValue)) {
      return Comparator.comparing(this::getStateText, String.CASE_INSENSITIVE_ORDER);
    }

    return Comparator.comparing(Bicycle::getModel, String.CASE_INSENSITIVE_ORDER);
  }

  public void setState(Bicycle bicycle, StateBicycle state) {
    if (bicycle == null || state == null) {
      return;
    }

    bicycle.setState(state);
    bicycleService.update(bicycle);
    loadBicycles();
  }

  private String safe(String value) {
    return value == null ? "" : value.toLowerCase(Locale.ROOT);
  }
}