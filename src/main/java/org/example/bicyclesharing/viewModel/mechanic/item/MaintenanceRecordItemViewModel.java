package org.example.bicyclesharing.viewModel.mechanic.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;

public class MaintenanceRecordItemViewModel {

  private final MaintenanceRecord record;

  private final StringProperty bikeText = new SimpleStringProperty();
  private final StringProperty typeText = new SimpleStringProperty();
  private final StringProperty descriptionText = new SimpleStringProperty();
  private final StringProperty resultText = new SimpleStringProperty();
  private final StringProperty dateText = new SimpleStringProperty();
  private final StringProperty actionText = new SimpleStringProperty();

  public MaintenanceRecordItemViewModel(
      MaintenanceRecord record,
      String bikeText,
      String typeText,
      String descriptionText,
      String resultText,
      String dateText,
      String actionText
  ) {
    this.record = record;
    this.bikeText.set(bikeText);
    this.typeText.set(typeText);
    this.descriptionText.set(descriptionText);
    this.resultText.set(resultText);
    this.dateText.set(dateText);
    this.actionText.set(actionText);
  }

  public MaintenanceRecord getRecord() {
    return record;
  }

  public StringProperty bikeTextProperty() {
    return bikeText;
  }

  public StringProperty typeTextProperty() {
    return typeText;
  }

  public StringProperty descriptionTextProperty() {
    return descriptionText;
  }

  public StringProperty resultTextProperty() {
    return resultText;
  }

  public StringProperty dateTextProperty() {
    return dateText;
  }

  public StringProperty actionTextProperty() {
    return actionText;
  }
}