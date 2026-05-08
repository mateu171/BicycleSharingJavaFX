package org.example.bicyclesharing.controller.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.util.ThemeManager;
import org.example.bicyclesharing.viewModel.SettingsViewModel;

public class SettingsController {

  private static final String LIGHT_THEME = "light";
  private static final String DARK_THEME = "dark";

  @FXML private Label titleLabel;
  @FXML private Label languageLabel;
  @FXML private Label themeLabel;

  @FXML private RadioButton lightThemeRadio;
  @FXML private RadioButton darkThemeRadio;

  @FXML private Button saveButton;
  @FXML private ComboBox<String> languageComboBox;

  private StackPane rootPane;
  private SettingsViewModel viewModel;

  @FXML
  private void initialize() {
    viewModel = new SettingsViewModel();

    bind();
  }

  public void setRootPane(StackPane rootPane) {
    this.rootPane = rootPane;
    applyTheme();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    languageLabel.textProperty().bind(viewModel.languageTextProperty());
    themeLabel.textProperty().bind(viewModel.themeTextProperty());

    lightThemeRadio.textProperty().bind(viewModel.themeRadioTextLightProperty());
    darkThemeRadio.textProperty().bind(viewModel.themeRadioTextDarkProperty());

    saveButton.textProperty().bind(viewModel.saveChangeButtonTextProperty());

    languageComboBox.setItems(viewModel.getLanguages());
    languageComboBox.valueProperty().bindBidirectional(
        viewModel.selectedLanguageProperty()
    );

    ToggleGroup themeGroup = new ToggleGroup();
    lightThemeRadio.setToggleGroup(themeGroup);
    darkThemeRadio.setToggleGroup(themeGroup);

    if (DARK_THEME.equals(viewModel.selectedThemeProperty().get())) {
      darkThemeRadio.setSelected(true);
    } else {
      lightThemeRadio.setSelected(true);
    }

    themeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal == darkThemeRadio) {
        viewModel.selectedThemeProperty().set(DARK_THEME);
      } else if (newVal == lightThemeRadio) {
        viewModel.selectedThemeProperty().set(LIGHT_THEME);
      }
    });
  }

  @FXML
  private void saveChange() {
    viewModel.saveChanges();
    applyTheme();
  }

  private void applyTheme() {
    if (rootPane == null || rootPane.getScene() == null) {
      return;
    }

    rootPane.getStylesheets().clear();
    rootPane.getStylesheets().add(
        getClass().getResource(ThemeManager.getSavedTheme()).toExternalForm()
    );
  }

  @FXML
  public void logout() {
    new NavigationService().openStartWindow();

    Stage stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }
}