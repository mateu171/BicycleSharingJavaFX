package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.ThemeManager;

public class SettingsController {

  @FXML
  private RadioButton lightThemeRadio;
  @FXML
  private RadioButton darkThemeRadio;
  @FXML
  private ComboBox languageComboBox;

  private StackPane rootPane;
  private MainMenuController mainMenuController;

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
     ThemeManager.saveTheme("dark");
   }else if(lightThemeRadio.isSelected())
   {
     ThemeManager.saveTheme("light");
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
   String savedTheme = ThemeManager.getCurrentTheme();
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
     rootPane.getStylesheets().clear();
      rootPane.getStylesheets().add(getClass().getResource(ThemeManager.getSavedTheme()).toExternalForm());
  }
  private void applyLang()
  {
    mainMenuController.applyLang();
  }
}
