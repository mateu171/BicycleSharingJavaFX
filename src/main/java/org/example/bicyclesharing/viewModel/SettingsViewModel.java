package org.example.bicyclesharing.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.ThemeManager;

public class SettingsViewModel {

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("settings.title");

  private final StringProperty languageText =
      LocalizationManager.getStringProperty("settings.language");

  private final StringProperty themeText =
      LocalizationManager.getStringProperty("settings.theme");

  private final StringProperty themeRadioTextLight =
      LocalizationManager.getStringProperty("settings.light");

  private final StringProperty themeRadioTextDark =
      LocalizationManager.getStringProperty("settings.dark");

  private final StringProperty saveChangeButtonText =
      LocalizationManager.getStringProperty("settings.save");

  private final ObservableList<String> languages =
      FXCollections.observableArrayList();

  private final StringProperty selectedLanguage =
      new SimpleStringProperty();

  private final StringProperty selectedTheme =
      new SimpleStringProperty();

  public SettingsViewModel() {
    updateLanguages();

    LocalizationManager.localeProperty().addListener((obs, oldVal, newVal) ->
        updateLanguages()
    );

    selectedTheme.set(ThemeManager.getCurrentTheme());

    if (LocalizationManager.getLocale().getLanguage().equals("uk")) {
      selectedLanguage.set(LocalizationManager.getStringByKey("lang.uk"));
    } else {
      selectedLanguage.set(LocalizationManager.getStringByKey("lang.en"));
    }
  }

  private void updateLanguages() {
    String current = selectedLanguage.get();

    languages.setAll(
        LocalizationManager.getStringByKey("lang.uk"),
        LocalizationManager.getStringByKey("lang.en")
    );

    if (current == null || current.isBlank()) {
      return;
    }

    if (current.equals(LocalizationManager.getStringByKey("lang.uk"))) {
      selectedLanguage.set(LocalizationManager.getStringByKey("lang.uk"));
    } else {
      selectedLanguage.set(LocalizationManager.getStringByKey("lang.en"));
    }
  }

  public void saveChanges() {
    ThemeManager.saveTheme(selectedTheme.get());

    if (selectedLanguage.get().equals(LocalizationManager.getStringByKey("lang.uk"))) {
      LocalizationManager.setLocale("uk");
    } else {
      LocalizationManager.setLocale("en");
    }
  }

  public ObservableList<String> getLanguages() {
    return languages;
  }

  public StringProperty selectedLanguageProperty() {
    return selectedLanguage;
  }

  public StringProperty selectedThemeProperty() {
    return selectedTheme;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty languageTextProperty() {
    return languageText;
  }

  public StringProperty themeTextProperty() {
    return themeText;
  }

  public StringProperty themeRadioTextLightProperty() {
    return themeRadioTextLight;
  }

  public StringProperty themeRadioTextDarkProperty() {
    return themeRadioTextDark;
  }

  public StringProperty saveChangeButtonTextProperty() {
    return saveChangeButtonText;
  }
}