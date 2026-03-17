package org.example.bicyclesharing.viewModel;

import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.util.LocalizationManager;

public class MapViewModel {

  public final StringProperty titleText = LocalizationManager.getStringProperty("map.title");
  public final StringProperty rentButtonText = LocalizationManager.getStringProperty("button.rent");
  public final StringProperty finishButtonText = LocalizationManager.getStringProperty("button.finish");
  public final StringProperty labelPrice = LocalizationManager.getStringProperty("label.price");

}
