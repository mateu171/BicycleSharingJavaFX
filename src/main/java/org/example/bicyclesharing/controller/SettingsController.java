package org.example.bicyclesharing.controller;

import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;
import org.example.bicyclesharing.util.LocalizationManager;

public class SettingsController {

  @FXML
  private RadioButton lightThemeRadio;
  @FXML
  private RadioButton darkThemeRadio;
  @FXML
  private ComboBox languageComboBox;

  private StackPane rootPane;
  private MainMenuController mainMenuController;

  private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
  private static final String THEME_KEY = "theme";

  public void setRootPane(StackPane rootPane,MainMenuController mainMenuController) {

    this.rootPane = rootPane;
    this.mainMenuController = mainMenuController;
    languageComboBox.getItems().addAll(LocalizationManager.getStringByKey("lang.uk"),
        LocalizationManager.getStringByKey("lang.en"));
    loadTheme();
  }

 public void saveChange() {
   if(darkThemeRadio.isSelected())
   {
     prefs.put(THEME_KEY,"dark");
   }else if(lightThemeRadio.isSelected())
   {
     prefs.put(THEME_KEY,"light");
   }
   int selectionIndex = languageComboBox.getSelectionModel().getSelectedIndex();

   if(selectionIndex == 0)
   {
     LocalizationManager.setLocale("uk");
   }else if(selectionIndex == 1)
   {
     LocalizationManager.setLocale("en");
   }

   applyTheme();
   applyLang();
 }

 private void loadTheme()
 {
   String savedTheme = prefs.get(THEME_KEY,"light");
   if(savedTheme.equals("dark"))
   {
     darkThemeRadio.setSelected(true);
   }
   else {
     lightThemeRadio.setSelected(true);
   }
   applyTheme();
 }

  private void applyTheme() {
    if (rootPane == null)
      return;

    rootPane.getStylesheets().clear();

    if (darkThemeRadio.isSelected()) {
      rootPane.getStylesheets().add(
          getClass().getResource("/org/example/bicyclesharing/css/dark-theme.css").toExternalForm()
      );
    } else {
      rootPane.getStylesheets().add(
          getClass().getResource("/org/example/bicyclesharing/css/style.css").toExternalForm()
      );
    }
  }
  private void applyLang()
  {
    mainMenuController.applyLang();
  }
}
