package org.example.bicyclesharing.viewModel.admin.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.util.LocalizationManager;

public class StationItemViewModel {

  private Station station;

  private final StringProperty nameText = new SimpleStringProperty();
  private final StringProperty coordinatesText = new SimpleStringProperty();
  private final StringProperty bicyclesCountText = new SimpleStringProperty();


  public StationItemViewModel(Station station)
  {
    this.station = station;

    nameText.set(station.getName());
    coordinatesText.set(
        LocalizationManager.getStringByKey("admin.stations.coords")
            + ": "
            + station.getLatitude()
            + ", "
            + station.getLongitude()
    );
    bicyclesCountText.set(
        LocalizationManager.getStringByKey("admin.stations.bicycles")
            + ": "
            + station.getBicyclesId().size()
    );
  }

  public Station getStation() {
    return station;
  }

  public StringProperty nameTextProperty() {
    return nameText;
  }

  public StringProperty coordinatesTextProperty() {
    return coordinatesText;
  }

  public StringProperty bicyclesCountTextProperty() {
    return bicyclesCountText;
  }
}
