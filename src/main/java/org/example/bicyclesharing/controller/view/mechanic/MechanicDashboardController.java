package org.example.bicyclesharing.controller.view.mechanic;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.Navigable;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.mechanic.MechanicDashboardViewModel;

public class MechanicDashboardController extends BaseController implements Navigable {

  @FXML private Label titleLabel;
  @FXML private Label subtitleLabel;

  @FXML private Label newIssuesTitleLabel;
  @FXML private Label newIssuesValueLabel;

  @FXML private Label inProgressIssuesTitleLabel;
  @FXML private Label inProgressIssuesValueLabel;

  @FXML private Label onMaintenanceTitleLabel;
  @FXML private Label onMaintenanceValueLabel;

  @FXML private Label needsInspectionTitleLabel;
  @FXML private Label needsInspectionValueLabel;

  @FXML private Label attentionTitleLabel;
  @FXML private Label latestActivityTitleLabel;
  @FXML private Label quickActionsTitleLabel;

  @FXML private Label attentionNewIssuesLabel;
  @FXML private Label attentionTechnicalIssuesLabel;
  @FXML private Label attentionResolvedIssuesLabel;
  @FXML private Label attentionMaintenanceRecordsLabel;
  @FXML private Label attentionUnavailableBicyclesLabel;

  @FXML private Label latestIssueLabel;
  @FXML private Label latestMaintenanceLabel;
  @FXML private Label latestInspectionLabel;

  @FXML private Button openIssuesButton;
  @FXML private Button openServiceButton;
  @FXML private Button openHistoryButton;

  private NavigationService navigationService;
  private MechanicDashboardViewModel viewModel;

  @Override
  public void setNavigation(NavigationService navigation) {
    this.navigationService = navigation;
  }

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new MechanicDashboardViewModel(
        currentUser,
        AppConfig.bicycleService(),
        AppConfig.bikeIssueService(),
        AppConfig.maintenanceRecordService()
    );
    bind();
    viewModel.loadAsync();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    subtitleLabel.textProperty().bind(viewModel.subtitleText);

    newIssuesTitleLabel.textProperty().bind(viewModel.newIssuesTitle);
    inProgressIssuesTitleLabel.textProperty().bind(viewModel.inProgressIssuesTitle);
    onMaintenanceTitleLabel.textProperty().bind(viewModel.onMaintenanceTitle);
    needsInspectionTitleLabel.textProperty().bind(viewModel.needsInspectionTitle);

    newIssuesValueLabel.textProperty().bind(viewModel.newIssuesValue);
    inProgressIssuesValueLabel.textProperty().bind(viewModel.inProgressIssuesValue);
    onMaintenanceValueLabel.textProperty().bind(viewModel.onMaintenanceValue);
    needsInspectionValueLabel.textProperty().bind(viewModel.needsInspectionValue);

    attentionTitleLabel.textProperty().bind(viewModel.attentionTitle);
    latestActivityTitleLabel.textProperty().bind(viewModel.latestActivityTitle);
    quickActionsTitleLabel.textProperty().bind(viewModel.quickActionsTitle);

    attentionNewIssuesLabel.textProperty().bind(viewModel.attentionNewIssuesText);
    attentionTechnicalIssuesLabel.textProperty().bind(viewModel.attentionTechnicalIssuesText);
    attentionResolvedIssuesLabel.textProperty().bind(viewModel.attentionResolvedIssuesText);
    attentionMaintenanceRecordsLabel.textProperty().bind(viewModel.attentionMaintenanceRecordsText);
    attentionUnavailableBicyclesLabel.textProperty().bind(viewModel.attentionUnavailableBicyclesText);

    latestIssueLabel.textProperty().bind(viewModel.latestIssueText);
    latestMaintenanceLabel.textProperty().bind(viewModel.latestMaintenanceText);
    latestInspectionLabel.textProperty().bind(viewModel.latestInspectionText);

    openIssuesButton.textProperty().bind(viewModel.openIssuesButtonText);
    openServiceButton.textProperty().bind(viewModel.openServiceButtonText);
    openHistoryButton.textProperty().bind(viewModel.openHistoryButtonText);
  }

  @FXML
  private void onOpenIssues() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/mechanic/MechanicIssuesView.fxml");
  }

  @FXML
  private void onOpenService() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/mechanic/MechanicServiceView.fxml");
  }

  @FXML
  private void onOpenHistory() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/mechanic/MaintenanceHistoryView.fxml");
  }
}