package org.example.bicyclesharing.controller.view.sidebar.Impl;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.bicyclesharing.controller.view.sidebar.Interface.SidebarController;
import org.example.bicyclesharing.controller.window.MainMenuController;
import org.example.bicyclesharing.viewModel.MainMenuViewModel;

public class MechanicSidebarController implements SidebarController {

  @FXML private Button btnMechanicIssues;
  @FXML private Button btnMechanicService;
  @FXML private Button btnMechanicHistory;
  @FXML private Button btnMechanicRecord;
  @FXML private Button btnDashboardMechanic;
  @FXML private Button btnSettings;

  private MainMenuController mainMenuController;
  private MainMenuViewModel mainMenuViewModel;

  @Override
  public void setMainMenuController(MainMenuController controller, MainMenuViewModel viewModel) {
       mainMenuController = controller;
       mainMenuViewModel = viewModel;
       binds();
  }


  @FXML
  private void onShowIssues()
  {
    mainMenuController.onShowMechanicIssues();
  }

  @FXML
  private void onShowService()
  {
    mainMenuController.onShowMechanicService();
  }

  @FXML
  private void onShowHistory()
  {
    mainMenuController.onShowMechanicHistory();
  }

  @FXML
  private void onShowAddRecord()
  {
    mainMenuController.onShowMechanicAddRecord();
  }

  @FXML
  private void onShowDashboard()
  {
       mainMenuController.onShowMechanicDashboard();
  }

  @FXML
  private void onShowSettings()
  {
    mainMenuController.onShowSettings();
  }

  private void binds() {
    btnDashboardMechanic.textProperty().bind(mainMenuViewModel.dashboardButtonText);
    btnMechanicIssues.textProperty().bind(mainMenuViewModel.mechanicIssuesButtonText);
    btnMechanicHistory.textProperty().bind(mainMenuViewModel.mechanicHistoryButtonText);
    btnMechanicService.textProperty().bind(mainMenuViewModel.mechanicServiceButtonText);
    btnMechanicRecord.textProperty().bind(mainMenuViewModel.mechanicRecordButtonText);
    btnSettings.textProperty().bind(mainMenuViewModel.settingsButtonText);
  }
}
