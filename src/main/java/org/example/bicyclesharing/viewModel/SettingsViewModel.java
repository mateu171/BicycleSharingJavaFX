package org.example.bicyclesharing.viewModel;

import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.util.LocalizationManager;

public class SettingsViewModel {
  public final StringProperty titleText = LocalizationManager.getStringProperty("settings.title");
  public final StringProperty comboItemUK = LocalizationManager.getStringProperty("lang.uk");
  public final StringProperty comboItemEN = LocalizationManager.getStringProperty("lang.en");
  public final StringProperty languageText = LocalizationManager.getStringProperty("settings.language");
  public final StringProperty themeText = LocalizationManager.getStringProperty("settings.theme");
  public final StringProperty themeRadioTextLight = LocalizationManager.getStringProperty("settings.light");
  public final StringProperty themeRadioTextDark = LocalizationManager.getStringProperty("settings.dark");
  public final StringProperty saveChangeButtonText = LocalizationManager.getStringProperty("settings.save");

}
