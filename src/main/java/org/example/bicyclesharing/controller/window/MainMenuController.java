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

  @FXML private HBox managerCustomersContainer;
  @FXML private Button btnManagerCustomers;
  @FXML private VBox sidebar;
  @FXML private Button btnMap;
  @FXML private Button btnProfile;
  @FXML private Button btnBalance;
  @FXML private Button btnHistory;
  @FXML private Button btnTransaction;
  @FXML private Button btnSettings;

  @FXML private Button btnUsers;
  @FXML private Button btnEmployees;
  @FXML private Button btnBicycles;
  @FXML private Button btnStations;

  @FXML private Button btnMechanicIssues;
  @FXML private Button btnMechanicService;
  @FXML private Button btnMechanicHistory;
  @FXML private Button btnMechanicRecord;

  @FXML private HBox mechanicIssuesContainer;
  @FXML private HBox mechanicServiceContainer;
  @FXML private HBox mechanicHistoryContainer;
  @FXML private HBox mechanicRecordContainer;

  @FXML private HBox adminUsersContainer;
  @FXML private HBox adminEmployeesContainer;
  @FXML private HBox adminBicyclesContainer;
  @FXML private HBox adminStationContainer;

  @FXML private HBox mapContainer;
  @FXML private HBox profileContainer;
  @FXML private HBox balanceContainer;
  @FXML private HBox historyContainer;
  @FXML private HBox transactionContainer;
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
    } else if(currentUser.getRole() == Role.CLIENT){
      onShowProfile();
    }
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
    btnUsers.textProperty().bind(viewModel.usersButtonText);
    btnEmployees.textProperty().bind(viewModel.employeesButtonText);
    btnBicycles.textProperty().bind(viewModel.bicyclesButtonText);
    btnStations.textProperty().bind(viewModel.stationButtonText);
    btnMechanicIssues.textProperty().bind(viewModel.mechanicIssuesButtonText);
    btnMechanicService.textProperty().bind(viewModel.mechanicServiceButtonText);
    btnMechanicHistory.textProperty().bind(viewModel.mechanicHistoryButtonText);
    btnMechanicRecord.textProperty().bind(viewModel.mechanicRecordButtonText);
    btnManagerCustomers.textProperty().bind(viewModel.managerCustomersButtonText);

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
    navigationService.load("/org/example/bicyclesharing/presentation/view/user/ProfileView.fxml");
  }

  @FXML
  public void onShowBalance() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/user/BalanceView.fxml");
  }

  @FXML
  public void onShowRideHistory() {
    navigationService.load(
        "/org/example/bicyclesharing/presentation/view/user/RideHistoryView.fxml");
  }

  @FXML
  public void onShowTransactions() {
    navigationService.load(
        "/org/example/bicyclesharing/presentation/view/user/TransactionView.fxml");
  }

  @FXML
  public void onShowSettings() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/SettingsView.fxml");
  }

  @FXML
  public void onShowMap() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/user/MapView.fxml");
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
  public void onShowManager() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/ManagerCustomersView.fxml");
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
    boolean isClient = currentUser != null && currentUser.getRole() == Role.CLIENT;
    boolean isManager = currentUser != null && currentUser.getRole() == Role.MANAGER;

    adminUsersContainer.setVisible(isAdmin);
    adminUsersContainer.setManaged(isAdmin);

    adminEmployeesContainer.setVisible(isAdmin);
    adminEmployeesContainer.setManaged(isAdmin);

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

    mapContainer.setVisible(isClient);
    mapContainer.setManaged(isClient);

    balanceContainer.setVisible(isClient);
    balanceContainer.setManaged(isClient);

    historyContainer.setVisible(isClient);
    historyContainer.setManaged(isClient);

    transactionContainer.setVisible(isClient);
    transactionContainer.setManaged(isClient);

    profileContainer.setVisible(isClient);
    profileContainer.setManaged(isClient);

    managerCustomersContainer.setVisible(isManager);
    managerCustomersContainer.setManaged(isManager);

    settingsContainer.setVisible(true);
    settingsContainer.setManaged(true);
  }

  @FXML
  public void onShowUsers() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/admin/UsersManagementView.fxml");
  }

  @FXML
  public void onShowEmployees() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/admin/EmployeeManagementView.fxml");
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
