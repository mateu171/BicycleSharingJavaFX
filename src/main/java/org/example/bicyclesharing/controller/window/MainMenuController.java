package org.example.bicyclesharing.controller.window;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.SidebarAnimation;
import org.example.bicyclesharing.util.ThemeManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.MainMenuViewModel;

public class MainMenuController extends BaseWindowController{
  @FXML
  private VBox sidebar;
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
  private Button btnSettings;
  private double xOffset = 0;
  private double yOffset = 0;

  private MainMenuViewModel viewModel;

  public void setCurrentUser(User currentUser) {
    navigationService.setCurrentUser(currentUser);
    onShowProfile();
  }

  @Override
  @FXML
  protected void initialize() {
    super.initialize();
    SidebarAnimation.applyHoverAnimation(sidebar, 180, 60);

    viewModel = new MainMenuViewModel();
    btnProfile.textProperty().bind(viewModel.profileButtonText);
    btnBalance.textProperty().bind(viewModel.balanceButtonText);
    btnHistory.textProperty().bind(viewModel.historyButtonText);
    btnMap.textProperty().bind(viewModel.mapButtonText);
    btnSettings.textProperty().bind(viewModel.settingsButtonText);
    btnTransaction.textProperty().bind(viewModel.transactionButtonText);
  }

  @Override
  protected void initializeWindow(StackPane contentPane) {
    contentPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene != null) {
        newScene.windowProperty().addListener((obsW, oldWindow, newWindow) -> {
          if (newWindow != null) {
            windowUtil = new WindowUtil((Stage) newWindow);
            applyTheme();
          }
        });
      }
    });
  }
  public void closeWindow()
  {
    windowUtil.close();
  }

  public void minimizeWindow()
  {
    windowUtil.minimize();
  }

  public void fullSize()
  {
   windowUtil.toggleFullSize();
  }

  @FXML
  public void onShowProfile() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/ProfileView.fxml");
  }

  @FXML
  public void onShowBalance() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/BalanceView.fxml");
  }

  @FXML
  public void onShowRideHistory() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/RideHistoryView.fxml");
  }

  @FXML
  public void onShowTransactions() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/TransactionView.fxml");
  }

  @FXML
  public void onShowSettings() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/SettingsView.fxml");
  }

  @FXML
  public void onShowMap() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/MapView.fxml");
  }

  private void applyTheme() {
    contentPane.getScene().getRoot().getStylesheets().clear();
    contentPane.getScene().getRoot().getStylesheets().add(getClass().getResource(ThemeManager.getSavedTheme()).toExternalForm());
  }

  @FXML
  private void handleMousePressed(MouseEvent event) {
    xOffset = event.getScreenX() - ((Stage)((Node)event.getSource()).getScene().getWindow()).getX();
    yOffset = event.getScreenY() - ((Stage)((Node)event.getSource()).getScene().getWindow()).getY();
  }

  @FXML
  private void handleMouseDragged(MouseEvent event) {
    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    stage.setX(event.getScreenX() - xOffset);
    stage.setY(event.getScreenY() - yOffset);
  }
}
