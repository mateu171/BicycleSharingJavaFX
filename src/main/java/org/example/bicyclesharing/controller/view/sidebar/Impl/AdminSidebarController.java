package org.example.bicyclesharing.controller.view.sidebar.Impl;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.bicyclesharing.controller.view.sidebar.Interface.SidebarController;
import org.example.bicyclesharing.controller.window.MainMenuController;
import org.example.bicyclesharing.viewModel.MainMenuViewModel;

public class AdminSidebarController implements SidebarController {

  @FXML private Button btnUsers;
  @FXML private Button btnBicycles;
  @FXML private Button btnStations;
  @FXML private Button btnDashboardAdmin;
  @FXML private Button btnSettings;

  private MainMenuViewModel mainMenuViewModel;

  private Runnable showDashboardAction;
  private Runnable showUsersAction;
  private Runnable showBicyclesAction;
  private Runnable showStationsAction;
  private Runnable showSettingsAction;

  @Override
  public void setMainMenuController(
      MainMenuController controller,
      MainMenuViewModel viewModel
  ) {
    this.mainMenuViewModel = viewModel;

    this.showDashboardAction = controller::onShowAdminDashboard;
    this.showUsersAction = controller::onShowUsers;
    this.showBicyclesAction = controller::onShowBicycles;
    this.showStationsAction = controller::onShowStations;
    this.showSettingsAction = controller::onShowSettings;

    bind();
  }

  private void bind() {
    btnUsers.textProperty().bind(mainMenuViewModel.usersButtonTextProperty());
    btnBicycles.textProperty().bind(mainMenuViewModel.bicyclesButtonTextProperty());
    btnDashboardAdmin.textProperty().bind(mainMenuViewModel.dashboardButtonTextProperty());
    btnStations.textProperty().bind(mainMenuViewModel.stationButtonTextProperty());
    btnSettings.textProperty().bind(mainMenuViewModel.settingsButtonTextProperty());
  }

  @FXML
  private void onShowDashboard() {
    run(showDashboardAction);
  }

  @FXML
  private void onShowUsers() {
    run(showUsersAction);
  }

  @FXML
  private void onShowBicycles() {
    run(showBicyclesAction);
  }

  @FXML
  private void onShowStations() {
    run(showStationsAction);
  }

  @FXML
  private void onShowSettings() {
    run(showSettingsAction);
  }

  private void run(Runnable action) {
    if (action != null) {
      action.run();
    }
  }
}