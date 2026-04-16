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
  private MainMenuController mainMenuController;
  private MainMenuViewModel mainMenuViewModel;

  @Override
  public void setMainMenuController(MainMenuController controller,MainMenuViewModel viewModel) {
    this.mainMenuController = controller;
    this.mainMenuViewModel = viewModel;
    binds();
  }

  @FXML
  private void onShowDashboard() {
    mainMenuController.onShowAdminDashboard();
  }

  @FXML
  private void onShowUsers() {
    mainMenuController.onShowUsers();
  }

  @FXML
  private void onShowBicycles() {
    mainMenuController.onShowBicycles();
  }

  @FXML
  private void onShowStations() {
    mainMenuController.onShowStations();
  }

  @FXML
  private void onShowSettings() {
    mainMenuController.onShowSettings();
  }

  private void binds()
  {
    btnUsers.textProperty().bind(mainMenuViewModel.usersButtonText);
    btnBicycles.textProperty().bind(mainMenuViewModel.bicyclesButtonText);
    btnDashboardAdmin.textProperty().bind(mainMenuViewModel.dashboardButtonText);
    btnStations.textProperty().bind(mainMenuViewModel.stationButtonText);
    btnSettings.textProperty().bind(mainMenuViewModel.settingsButtonText);
  }
}