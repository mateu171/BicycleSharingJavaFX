package org.example.bicyclesharing.controller.window;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.bicyclesharing.controller.view.sidebar.Interface.SidebarController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.util.SidebarAnimation;
import org.example.bicyclesharing.util.ThemeManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.MainMenuViewModel;

public class MainMenuController extends BaseWindowController{

  @FXML private VBox sidebar;
  @FXML private VBox menuContent;

  private double xOffset = 0;
  private double yOffset = 0;

  private MainMenuViewModel viewModel;

  public void setCurrentUser(User currentUser) throws IOException {
    navigationService.setCurrentUser(currentUser);
    loadMenuByRole(currentUser);

    if (currentUser.getRole() == Role.ADMIN) {
      onShowAdminDashboard();
    } else if (currentUser.getRole() == Role.MECHANIC) {
      onShowMechanicDashboard();
    } else if (currentUser.getRole() == Role.MANAGER) {
      onShowManagerDashboard();
    }
  }

  @Override
  @FXML
  protected void initialize() {
    super.initialize();
    SidebarAnimation.applyHoverAnimation(sidebar, 180, 60);
    viewModel = new MainMenuViewModel();
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
  public void onShowSettings() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/SettingsView.fxml");
  }

  @FXML
  public void onShowMechanicIssues() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/mechanic/MechanicIssuesView.fxml");
  }

  @FXML
  public void onShowMechanicService() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/mechanic/MechanicServiceView.fxml");
  }

  @FXML
  public void onShowMechanicAddRecord() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/mechanic/AddMaintenanceRecordView.fxml");
  }

  @FXML
  public void onShowMechanicHistory() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/mechanic/MaintenanceHistoryView.fxml");
  }

  @FXML
  public void onShowCustomerManager() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/ManagerCustomersView.fxml");
  }

  @FXML
  public void onShowReservation() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/ManagerReservationsView.fxml");
  }
  @FXML
  public void onShowActiveRentals() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/ManagerActiveRentalsView.fxml");
  }

  @FXML
  public void onShowAdminDashboard() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/admin/AdminDashboardView.fxml");
  }

  @FXML
  public void onShowManagerDashboard() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/ManagerDashboardView.fxml");
  }

  @FXML
  public void onShowMechanicDashboard() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/mechanic/MechanicDashboardView.fxml");
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

  private void loadMenuByRole(User currentUser) throws IOException {
    String fxml;

    if (currentUser.getRole() == Role.ADMIN) {
      fxml = "/org/example/bicyclesharing/presentation/view/sidebar/AdminSidebarView.fxml";
    } else if (currentUser.getRole() == Role.MECHANIC) {
      fxml = "/org/example/bicyclesharing/presentation/view/sidebar/MechanicSidebarView.fxml";
    } else {
      fxml = "/org/example/bicyclesharing/presentation/view/sidebar/ManagerSidebarView.fxml";
    }

      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
      VBox menu = loader.load();

      Object controller = loader.getController();
      if (controller instanceof SidebarController sidebarController) {
        sidebarController.setMainMenuController(this,viewModel);
      }

      menuContent.getChildren().setAll(menu);
  }

  @FXML
  public void onShowUsers() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/admin/UsersManagementView.fxml");
  }
  @FXML
  public void onShowBicycles() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/admin/BicyclesManagementView.fxml");
  }
  @FXML
  public void onShowStations() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/admin/StationManagementView.fxml");
  }
}
