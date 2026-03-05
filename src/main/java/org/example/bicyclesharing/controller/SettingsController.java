package org.example.bicyclesharing.controller;

import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;

public class SettingsController {

  @FXML
  private RadioButton lightThemeRadio;
  @FXML
  private RadioButton darkThemeRadio;

  private StackPane rootPane;

  private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
  private static final String THEME_KEY = "theme";

  public void setRootPane(StackPane rootPane) {

    this.rootPane = rootPane;
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

   applyTheme();
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
}
