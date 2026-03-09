package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.ThemeManager;
import org.example.bicyclesharing.viewModel.SettingsViewModel;

public class SettingsController {

  @FXML
  private Label titleLabel;

  @FXML
  private Label languageLabel;

  @FXML
  private Label themeLabel;

  @FXML
  private RadioButton lightThemeRadio;
  @FXML
  private Button saveButton;

  @FXML
  private RadioButton darkThemeRadio;
  @FXML
  private ComboBox languageComboBox;

  private StackPane rootPane;
  private SettingsViewModel viewModel;

  public void setRootPane(StackPane rootPane) {

    this.rootPane = rootPane;
    loadTheme();
  }

  @FXML
  private void initialize()
  {
    viewModel = new SettingsViewModel();

    updateCombo();

    if (LocalizationManager.getLocale().getLanguage().equals("uk")) {
      languageComboBox.getSelectionModel().select(0);
    } else {
      languageComboBox.getSelectionModel().select(1);
    }

    LocalizationManager.localeProperty().addListener((obs, oldVal, newVal) -> {
      updateCombo();
    });

    lightThemeRadio.textProperty().bind(viewModel.themeRadioTextLight);
    darkThemeRadio.textProperty().bind(viewModel.themeRadioTextDark);
    titleLabel.textProperty().bind(viewModel.titleText);
    languageLabel.textProperty().bind(viewModel.languageText);
    themeLabel.textProperty().bind(viewModel.themeText);
    saveButton.textProperty().bind(viewModel.saveChangeButtonText);

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

  private void updateCombo() {
    int selectedIndex = languageComboBox.getSelectionModel().getSelectedIndex();

    languageComboBox.getItems().setAll(
        viewModel.comboItemUK.get(),
        viewModel.comboItemEN.get()
    );

    if (selectedIndex >= 0) {
      languageComboBox.getSelectionModel().select(selectedIndex);
    }
  }
}
