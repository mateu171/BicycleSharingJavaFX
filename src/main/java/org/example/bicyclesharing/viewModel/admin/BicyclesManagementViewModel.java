package org.example.bicyclesharing.viewModel.admin;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class BicyclesManagementViewModel extends BaseViewModel {

  private final BicycleService bicycleService;
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

  public BicyclesManagementViewModel(User currentUser, BicycleService bicycleService) {
    super(currentUser);
    this.bicycleService = bicycleService;
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
    List<Bicycle> allBicycles = bicycleService.getAll();

    String search = searchText.get() == null ? "" : searchText.get().trim().toLowerCase(Locale.ROOT);
    String stateFilter = selectedStateFilter.get() == null ? "ALL" : selectedStateFilter.get();

    List<Bicycle> filtered = allBicycles.stream()
        .filter(bike -> {
          boolean matchesSearch =
              search.isEmpty()
                  || bike.getModel().toLowerCase(Locale.ROOT).contains(search);

          boolean matchesState =
              stateFilter.equals("ALL")
                  || bike.getState().name().equals(stateFilter);

          return matchesSearch && matchesState;
        })
        .collect(Collectors.toList());

    bicycles.setAll(filtered);
    updateCount();
  }

  public void deleteBicycle(Bicycle bicycle) {
    if (bicycle == null) return;
    bicycleService.deleteById(bicycle.getId());
    applyFilters();
  }

  public void changeState(Bicycle bicycle, StateBicycle newState) {
    if (bicycle == null || newState == null) return;

    bicycle.setState(newState);
    bicycleService.update(bicycle);
    applyFilters();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("admin.bicycles.count") + ": " + bicycles.size()
    );
  }
}