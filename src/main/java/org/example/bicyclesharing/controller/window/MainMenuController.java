package org.example.bicyclesharing.controller.window;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.util.SidebarAnimation;
import org.example.bicyclesharing.util.ThemeManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.MainMenuViewModel;

public class MainMenuController extends BaseWindowController{

  @FXML private VBox sidebar;
  @FXML private Button btnSettings;

  @FXML private Button btnUsers;
  @FXML private Button btnBicycles;
  @FXML private Button btnStations;

  @FXML private Button btnManagerCustomers;
  @FXML private Button btnManagerReservation;
  @FXML private Button btnManagerActiveRentals;

  @FXML private Button btnMechanicIssues;
  @FXML private Button btnMechanicService;
  @FXML private Button btnMechanicHistory;
  @FXML private Button btnMechanicRecord;

  @FXML private HBox managerCustomersContainer;
  @FXML private HBox managerReservationContainer;
  @FXML private HBox managerAcitveRentalsContainer;

  @FXML private HBox mechanicIssuesContainer;
  @FXML private HBox mechanicServiceContainer;
  @FXML private HBox mechanicHistoryContainer;
  @FXML private HBox mechanicRecordContainer;

  @FXML private HBox adminUsersContainer;
  @FXML private HBox adminBicyclesContainer;
  @FXML private HBox adminStationContainer;

  @FXML private HBox settingsContainer;

  private double xOffset = 0;
  private double yOffset = 0;

  private MainMenuViewModel viewModel;

  public void setCurrentUser(User currentUser) {
    navigationService.setCurrentUser(currentUser);
    configureMenuByRole(currentUser);

    if (currentUser.getRole() == Role.ADMIN) {
      onShowUsers();
    } else if (currentUser.getRole() == Role.MECHANIC) {
      onShowMechanicIssues();
    } else if (currentUser.getRole() == Role.MANAGER) {
      onShowCustomerManager();
    }
  }

  @Override
  @FXML
  protected void initialize() {
    super.initialize();
    SidebarAnimation.applyHoverAnimation(sidebar, 180, 60);

    viewModel = new MainMenuViewModel();
    btnSettings.textProperty().bind(viewModel.settingsButtonText);
    btnUsers.textProperty().bind(viewModel.usersButtonText);
    btnBicycles.textProperty().bind(viewModel.bicyclesButtonText);
    btnStations.textProperty().bind(viewModel.stationButtonText);
    btnMechanicIssues.textProperty().bind(viewModel.mechanicIssuesButtonText);
    btnMechanicService.textProperty().bind(viewModel.mechanicServiceButtonText);
    btnMechanicHistory.textProperty().bind(viewModel.mechanicHistoryButtonText);
    btnMechanicRecord.textProperty().bind(viewModel.mechanicRecordButtonText);
    btnManagerCustomers.textProperty().bind(viewModel.managerCustomersButtonText);
    btnManagerReservation.textProperty().bind(viewModel.managerReservationsButtonText);
    btnManagerActiveRentals.textProperty().bind(viewModel.managerActiveRentalsButtonText);
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

  private void configureMenuByRole(User currentUser) {
    boolean isAdmin = currentUser != null && currentUser.getRole() == Role.ADMIN;
    boolean isMechanic = currentUser != null && currentUser.getRole() == Role.MECHANIC;
    boolean isManager = currentUser != null && currentUser.getRole() == Role.MANAGER;

    adminUsersContainer.setVisible(isAdmin);
    adminUsersContainer.setManaged(isAdmin);

    adminBicyclesContainer.setVisible(isAdmin);
    adminBicyclesContainer.setManaged(isAdmin);

    adminStationContainer.setVisible(isAdmin);
    adminStationContainer.setManaged(isAdmin);

    mechanicIssuesContainer.setVisible(isMechanic);
    mechanicIssuesContainer.setManaged(isMechanic);

    mechanicServiceContainer.setVisible(isMechanic);
    mechanicServiceContainer.setManaged(isMechanic);

    mechanicHistoryContainer.setVisible(isMechanic);
    mechanicHistoryContainer.setManaged(isMechanic);

    mechanicRecordContainer.setVisible(isMechanic);
    mechanicRecordContainer.setManaged(isMechanic);

    managerCustomersContainer.setVisible(isManager);
    managerCustomersContainer.setManaged(isManager);

    managerReservationContainer.setVisible(isManager);
    managerReservationContainer.setManaged(isManager);

    managerAcitveRentalsContainer.setVisible(isManager);
    managerAcitveRentalsContainer.setManaged(isManager);

    settingsContainer.setVisible(true);
    settingsContainer.setManaged(true);
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
