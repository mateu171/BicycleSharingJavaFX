package org.example.bicyclesharing.viewModel;

import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.util.LocalizationManager;

public class MainMenuViewModel {

  private final StringProperty settingsButtonText =
      LocalizationManager.getStringProperty("menu.settings");

  private final StringProperty usersButtonText =
      LocalizationManager.getStringProperty("menu.users");

  private final StringProperty bicyclesButtonText =
      LocalizationManager.getStringProperty("menu.bicycles");

  private final StringProperty stationButtonText =
      LocalizationManager.getStringProperty("menu.stations");

  private final StringProperty dashboardButtonText =
      LocalizationManager.getStringProperty("menu.dashboard");

  private final StringProperty mechanicIssuesButtonText =
      LocalizationManager.getStringProperty("menu.mechanic.issues");

  private final StringProperty mechanicServiceButtonText =
      LocalizationManager.getStringProperty("menu.mechanic.service");

  private final StringProperty mechanicHistoryButtonText =
      LocalizationManager.getStringProperty("menu.mechanic.history");

  private final StringProperty mechanicRecordButtonText =
      LocalizationManager.getStringProperty("menu.mechanic.add_record");

  private final StringProperty managerCustomersButtonText =
      LocalizationManager.getStringProperty("manager.customer");

  private final StringProperty managerReservationsButtonText =
      LocalizationManager.getStringProperty("menu.reservations");

  private final StringProperty managerActiveRentalsButtonText =
      LocalizationManager.getStringProperty("menu.rentals");

  public StringProperty settingsButtonTextProperty() {
    return settingsButtonText;
  }

  public StringProperty usersButtonTextProperty() {
    return usersButtonText;
  }

  public StringProperty bicyclesButtonTextProperty() {
    return bicyclesButtonText;
  }

  public StringProperty stationButtonTextProperty() {
    return stationButtonText;
  }

  public StringProperty dashboardButtonTextProperty() {
    return dashboardButtonText;
  }

  public StringProperty mechanicIssuesButtonTextProperty() {
    return mechanicIssuesButtonText;
  }

  public StringProperty mechanicServiceButtonTextProperty() {
    return mechanicServiceButtonText;
  }

  public StringProperty mechanicHistoryButtonTextProperty() {
    return mechanicHistoryButtonText;
  }

  public StringProperty mechanicRecordButtonTextProperty() {
    return mechanicRecordButtonText;
  }

  public StringProperty managerCustomersButtonTextProperty() {
    return managerCustomersButtonText;
  }

  public StringProperty managerReservationsButtonTextProperty() {
    return managerReservationsButtonText;
  }

  public StringProperty managerActiveRentalsButtonTextProperty() {
    return managerActiveRentalsButtonText;
  }
}