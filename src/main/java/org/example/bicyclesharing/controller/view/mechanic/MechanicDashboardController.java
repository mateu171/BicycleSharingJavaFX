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
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    subtitleLabel.textProperty().bind(viewModel.subtitleTextProperty());

    newIssuesTitleLabel.textProperty().bind(viewModel.newIssuesTitleProperty());
    inProgressIssuesTitleLabel.textProperty().bind(viewModel.inProgressIssuesTitleProperty());
    onMaintenanceTitleLabel.textProperty().bind(viewModel.onMaintenanceTitleProperty());
    needsInspectionTitleLabel.textProperty().bind(viewModel.needsInspectionTitleProperty());

    newIssuesValueLabel.textProperty().bind(viewModel.newIssuesValueProperty());
    inProgressIssuesValueLabel.textProperty().bind(viewModel.inProgressIssuesValueProperty());
    onMaintenanceValueLabel.textProperty().bind(viewModel.onMaintenanceValueProperty());
    needsInspectionValueLabel.textProperty().bind(viewModel.needsInspectionValueProperty());

    attentionTitleLabel.textProperty().bind(viewModel.attentionTitleProperty());
    latestActivityTitleLabel.textProperty().bind(viewModel.latestActivityTitleProperty());
    quickActionsTitleLabel.textProperty().bind(viewModel.quickActionsTitleProperty());

    attentionNewIssuesLabel.textProperty().bind(viewModel.attentionNewIssuesTextProperty());
    attentionTechnicalIssuesLabel.textProperty().bind(viewModel.attentionTechnicalIssuesTextProperty());
    attentionResolvedIssuesLabel.textProperty().bind(viewModel.attentionResolvedIssuesTextProperty());
    attentionMaintenanceRecordsLabel.textProperty().bind(viewModel.attentionMaintenanceRecordsTextProperty());
    attentionUnavailableBicyclesLabel.textProperty().bind(viewModel.attentionUnavailableBicyclesTextProperty());

    latestIssueLabel.textProperty().bind(viewModel.latestIssueTextProperty());
    latestMaintenanceLabel.textProperty().bind(viewModel.latestMaintenanceTextProperty());
    latestInspectionLabel.textProperty().bind(viewModel.latestInspectionTextProperty());

    openIssuesButton.textProperty().bind(viewModel.openIssuesButtonTextProperty());
    openServiceButton.textProperty().bind(viewModel.openServiceButtonTextProperty());
    openHistoryButton.textProperty().bind(viewModel.openHistoryButtonTextProperty());
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