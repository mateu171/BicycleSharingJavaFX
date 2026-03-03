package org.example.bicyclesharing.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;

public class SettingsController {

  @FXML
  private RadioButton lightThemeRadio;
  @FXML
  private RadioButton darkThemeRadio;
  @FXML
  private Button saveButton;

  private StackPane rootPane;

  public void setRootPane(StackPane rootPane) {
    this.rootPane = rootPane;
  }

 public void saveChange() {
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
