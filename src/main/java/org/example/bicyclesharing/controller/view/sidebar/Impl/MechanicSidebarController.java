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

  private MainMenuViewModel mainMenuViewModel;

  private Runnable showDashboardAction;
  private Runnable showIssuesAction;
  private Runnable showServiceAction;
  private Runnable showHistoryAction;
  private Runnable showAddRecordAction;
  private Runnable showSettingsAction;

  @Override
  public void setMainMenuController(
      MainMenuController controller,
      MainMenuViewModel viewModel
  ) {
    this.mainMenuViewModel = viewModel;

    this.showDashboardAction = controller::onShowMechanicDashboard;
    this.showIssuesAction = controller::onShowMechanicIssues;
    this.showServiceAction = controller::onShowMechanicService;
    this.showHistoryAction = controller::onShowMechanicHistory;
    this.showAddRecordAction = controller::onShowMechanicAddRecord;
    this.showSettingsAction = controller::onShowSettings;

    bind();
  }

  private void bind() {
    btnDashboardMechanic.textProperty().bind(mainMenuViewModel.dashboardButtonTextProperty());
    btnMechanicIssues.textProperty().bind(mainMenuViewModel.mechanicIssuesButtonTextProperty());
    btnMechanicHistory.textProperty().bind(mainMenuViewModel.mechanicHistoryButtonTextProperty());
    btnMechanicService.textProperty().bind(mainMenuViewModel.mechanicServiceButtonTextProperty());
    btnMechanicRecord.textProperty().bind(mainMenuViewModel.mechanicRecordButtonTextProperty());
    btnSettings.textProperty().bind(mainMenuViewModel.settingsButtonTextProperty());
  }

  @FXML
  private void onShowIssues() {
    run(showIssuesAction);
  }

  @FXML
  private void onShowService() {
    run(showServiceAction);
  }

  @FXML
  private void onShowHistory() {
    run(showHistoryAction);
  }

  @FXML
  private void onShowAddRecord() {
    run(showAddRecordAction);
  }

  @FXML
  private void onShowDashboard() {
    run(showDashboardAction);
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