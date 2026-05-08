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

  private MainMenuViewModel mainMenuViewModel;

  private Runnable showDashboardAction;
  private Runnable showCustomersAction;
  private Runnable showReservationsAction;
  private Runnable showActiveRentalsAction;
  private Runnable showSettingsAction;

  @Override
  public void setMainMenuController(
      MainMenuController controller,
      MainMenuViewModel viewModel
  ) {
    this.mainMenuViewModel = viewModel;

    this.showDashboardAction = controller::onShowManagerDashboard;
    this.showCustomersAction = controller::onShowCustomerManager;
    this.showReservationsAction = controller::onShowReservation;
    this.showActiveRentalsAction = controller::onShowActiveRentals;
    this.showSettingsAction = controller::onShowSettings;

    bind();
  }

  private void bind() {
    btnSettings.textProperty().bind(mainMenuViewModel.settingsButtonTextProperty());
    btnManagerCustomers.textProperty().bind(mainMenuViewModel.managerCustomersButtonTextProperty());
    btnManagerReservation.textProperty().bind(mainMenuViewModel.managerReservationsButtonTextProperty());
    btnManagerActiveRentals.textProperty().bind(mainMenuViewModel.managerActiveRentalsButtonTextProperty());
    btnDashBoardManager.textProperty().bind(mainMenuViewModel.dashboardButtonTextProperty());
  }

  @FXML
  private void onShowDashboard() {
    run(showDashboardAction);
  }

  @FXML
  private void onShowActiveRentals() {
    run(showActiveRentalsAction);
  }

  @FXML
  private void onShowCustomers() {
    run(showCustomersAction);
  }

  @FXML
  private void onShowReservations() {
    run(showReservationsAction);
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