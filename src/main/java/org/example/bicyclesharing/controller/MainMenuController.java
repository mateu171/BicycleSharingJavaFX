package org.example.bicyclesharing.controller;

import java.io.IOException;
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
import org.example.bicyclesharing.util.SidebarAnimation;
import org.example.bicyclesharing.util.ThemeManager;
import org.example.bicyclesharing.viewModel.MainMenuViewModel;

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
  private MainMenuViewModel viewModel;

  public void setCurrentUser(User currentUser) {

    this.currentUser = currentUser;
    onShowProfile();
  }

  @FXML
  public void initialize()
  {
    sidebar.sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene != null) {
        applyTheme();
      }
    });
    SidebarAnimation.applyHoverAnimation(sidebar, 180, 60);

    viewModel = new MainMenuViewModel();
    btnProfile.textProperty().bind(viewModel.profileButtonText);
    btnBalance.textProperty().bind(viewModel.balanceButtonText);
    btnGuide.textProperty().bind(viewModel.guideButtonText);
    btnHistory.textProperty().bind(viewModel.historyButtonText);
    btnMap.textProperty().bind(viewModel.mapButtonText);
    btnSettings.textProperty().bind(viewModel.settingsButtonText);
    btnTransaction.textProperty().bind(viewModel.transactionButtonText);
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

  @FXML
  public void onShowProfile() {
    loadView("/org/example/bicyclesharing/presentation/ProfileView.fxml");
  }

  @FXML
  public void onShowBalance() {
    loadView("/org/example/bicyclesharing/presentation/BalanceView.fxml");
  }

  @FXML
  public void onShowRideHistory() {
    loadView("/org/example/bicyclesharing/presentation/RideHistoryView.fxml");
  }

  @FXML
  public void onShowTransactions() {
    loadView("/org/example/bicyclesharing/presentation/TransactionView.fxml");
  }

  @FXML
  public void onShowSettings() {
    loadView("/org/example/bicyclesharing/presentation/SettingsView.fxml");
  }

  @FXML
  public void onShowMap() {
    loadView("/org/example/bicyclesharing/presentation/MapView.fxml");
  }

  private void loadView(String path) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
      Parent view = loader.load();

      Object controller = loader.getController();
      if (controller instanceof BaseController baseController) {
        baseController.setCurrentUser(currentUser);
      }
      if (controller instanceof SettingsController settingsController) {
        settingsController.setRootPane((StackPane) closeButton.getScene().getRoot());
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
}
