package org.example.bicyclesharing.controller.view.sidebar.Impl;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.bicyclesharing.controller.view.sidebar.Interface.SidebarController;
import org.example.bicyclesharing.controller.window.MainMenuController;
import org.example.bicyclesharing.viewModel.MainMenuViewModel;

public class ManagerSidebarController implements SidebarController {

  @FXML private Button btnDashBoardManager;
  @FXML private Button btnManagerCustomers;
  @FXML private Button btnManagerReservation;
  @FXML private Button btnManagerActiveRentals;
  @FXML private Button btnSettings;
  private MainMenuController mainMenuController;
  private MainMenuViewModel mainMenuViewModel;
  @Override
  public void setMainMenuController(MainMenuController controller, MainMenuViewModel viewModel) {
    this.mainMenuController = controller;
    this.mainMenuViewModel = viewModel;
    binds();
  }

  @FXML
  private void onShowDashboard()
  {
      mainMenuController.onShowManagerDashboard();
  }

  @FXML
  private void onShowActiveRentals()
  {
      mainMenuController.onShowActiveRentals();
  }
  @FXML
  private void onShowCustomers()
  {
    mainMenuController.onShowCustomerManager();
  }
  @FXML
  private void onShowSettings()
  {
    mainMenuController.onShowSettings();
  }

  @FXML
  private void onShowReservations()
  {
    mainMenuController.onShowReservation();
  }

  private void binds()
  {
    btnSettings.textProperty().bind(mainMenuViewModel.settingsButtonText);
    btnManagerCustomers.textProperty().bind(mainMenuViewModel.managerCustomersButtonText);
    btnManagerReservation.textProperty().bind(mainMenuViewModel.managerReservationsButtonText);
    btnManagerActiveRentals.textProperty().bind(mainMenuViewModel.managerActiveRentalsButtonText);
    btnDashBoardManager.textProperty().bind(mainMenuViewModel.dashboardButtonText);
  }
}
