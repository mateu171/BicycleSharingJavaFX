package org.example.bicyclesharing.viewModel;

import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.util.LocalizationManager;

public class MainMenuViewModel {
  public final StringProperty mapButtonText = LocalizationManager.getStringProperty("menu.map");
  public final StringProperty profileButtonText = LocalizationManager.getStringProperty("menu.profile");
  public final StringProperty balanceButtonText = LocalizationManager.getStringProperty("menu.balance");
  public final StringProperty settingsButtonText = LocalizationManager.getStringProperty("menu.settings");
  public final StringProperty transactionButtonText = LocalizationManager.getStringProperty("menu.transactions");
  public final StringProperty historyButtonText = LocalizationManager.getStringProperty("menu.history");

  public final StringProperty usersButtonText = LocalizationManager.getStringProperty("menu.users");
  public final StringProperty employeesButtonText = LocalizationManager.getStringProperty("menu.employees");
  public final StringProperty bicyclesButtonText = LocalizationManager.getStringProperty("menu.bicycles");
}
