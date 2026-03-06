package org.example.bicyclesharing.controller;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.ThemeManager;

public class MainMenuController {

  @FXML
  private Button closeButton;
  @FXML
  private Button minimizeButton;
  @FXML
  private Button fullSizeButton;
  @FXML
  private VBox sidebar;
  @FXML
  private StackPane contentPane;
  @FXML
  private Button btnMap;
  @FXML
  private Button btnProfile;
  @FXML
  private Button btnBalance;
  @FXML
  private Button btnHistory;
  @FXML
  private Button btnTransaction;
  @FXML
  private Button btnGuide;
  @FXML
  private Button btnSettings;

  private User currentUser;
  private String currentView;

  private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
  private static final String THEME_KEY = "theme";

  public void setCurrentUser(User currentUser) {

    this.currentUser = currentUser;
    onShowProfile();
    applyTheme();
  }

  @FXML
  public void initialize()
  {
//    sidebar.sceneProperty().addListener((obs, oldScene, newScene) -> {
//      if (newScene != null) {
//        applyTheme();
//      }
//    });
    double expandedWidth = 180;
    double collapsedWidth = 60;

    sidebar.setOnMouseEntered(e -> {
      Timeline expand = new Timeline(
          new KeyFrame(Duration.millis(300),
              new KeyValue(sidebar.prefWidthProperty(), expandedWidth, Interpolator.EASE_BOTH)
          )
      );
      expand.play();
    });
    sidebar.setOnMouseExited(e -> {
      Timeline collapse = new Timeline(
          new KeyFrame(Duration.millis(300),
              new KeyValue(sidebar.prefWidthProperty(), collapsedWidth, Interpolator.EASE_BOTH)
          )
      );
      collapse.play();
    });
  }
  public void closeWindow()
  {
    Stage stage = (Stage) closeButton.getScene().getWindow();
    stage.close();
  }

  public void minimizeWindow()
  {
    Stage stage = (Stage) minimizeButton.getScene().getWindow();
    stage.setIconified(true);
  }

  public void fullSize()
  {
    Stage stage = (Stage) fullSizeButton.getScene().getWindow();
    stage.setMaximized(!stage.isMaximized());
  }

  public void onShowProfile()
  {
    load("/org/example/bicyclesharing/presentation/ProfileView.fxml");
  }
  public void onShowBalance() {
    load("/org/example/bicyclesharing/presentation/BalanceView.fxml");
  }
  public void onShowRideHistory() {
    load("/org/example/bicyclesharing/presentation/RideHistoryView.fxml");
  }
  public void onShowTransactions() {
    load("/org/example/bicyclesharing/presentation/TransactionView.fxml");
  }
  public void onShowSettings() {
    load("/org/example/bicyclesharing/presentation/SettingsView.fxml");
  }
  public void onShowMap(){
    load("/org/example/bicyclesharing/presentation/MapView.fxml");
  }

  private void load(String path) {
    try {
      currentView = path;
      FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
      Parent view = loader.load();

      Object controller = loader.getController();
      if (controller instanceof ProfileController profileController) {
        profileController.setCurrentUser(currentUser);
      }
      if (controller instanceof BalanceController balanceController) {
        balanceController.setCurrentUser(currentUser);
      }
      if (controller instanceof RideHistoryController rideHistoryController) {
        rideHistoryController.setCurrentUser(currentUser);
      }
      if (controller instanceof TransactionController transactionController) {
        transactionController.setCurrentUser(currentUser);
      }
      if (controller instanceof SettingsController settingsController) {
        settingsController.setRootPane((StackPane) closeButton.getScene().getRoot(),this);
      }


      contentPane.getChildren().setAll(view);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void applyTheme() {
    closeButton.getScene().getRoot().getStylesheets().clear();
    closeButton.getScene().getRoot().getStylesheets().add(getClass().getResource(ThemeManager.getSavedTheme()).toExternalForm());
  }
  public void applyLang() {
    btnMap.setText(LocalizationManager.getStringByKey("menu.map"));
    btnProfile.setText(LocalizationManager.getStringByKey("menu.profile"));
    btnBalance.setText(LocalizationManager.getStringByKey("menu.balance"));
    btnHistory.setText(LocalizationManager.getStringByKey("menu.history"));
    btnTransaction.setText(LocalizationManager.getStringByKey("menu.transactions"));
    btnGuide.setText(LocalizationManager.getStringByKey("menu.guide"));
    btnSettings.setText(LocalizationManager.getStringByKey("menu.settings"));

    // Також перезавантажуємо поточний contentPane
    reloadCurrentView();
  }

  private void reloadCurrentView()
  {
    load(currentView);
  }

}
