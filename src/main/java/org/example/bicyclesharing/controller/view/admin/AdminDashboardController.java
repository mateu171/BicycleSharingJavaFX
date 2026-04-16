package org.example.bicyclesharing.controller.view.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.Navigable;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.admin.AdminDashboardViewModel;

public class AdminDashboardController extends BaseController implements Navigable {

  @FXML private Label titleLabel;
  @FXML private Label subtitleLabel;

  @FXML private Label totalUsersTitleLabel;
  @FXML private Label totalUsersValueLabel;

  @FXML private Label totalBicyclesTitleLabel;
  @FXML private Label totalBicyclesValueLabel;

  @FXML private Label activeRentalsTitleLabel;
  @FXML private Label activeRentalsValueLabel;

  @FXML private Label activeReservationsTitleLabel;
  @FXML private Label activeReservationsValueLabel;

  @FXML private Label attentionTitleLabel;
  @FXML private Label latestActivityTitleLabel;
  @FXML private Label quickActionsTitleLabel;

  @FXML private Label needsInspectionLabel;
  @FXML private Label onMaintenanceLabel;
  @FXML private Label unavailableLabel;
  @FXML private Label newIssuesLabel;
  @FXML private Label totalStationsLabel;

  @FXML private Label latestRentalLabel;
  @FXML private Label latestReservationLabel;
  @FXML private Label latestIssueLabel;

  @FXML private Button openUsersButton;
  @FXML private Button openBicyclesButton;
  @FXML private Button openStationsButton;

  private NavigationService navigationService;
  private AdminDashboardViewModel viewModel;

  @Override
  public void setNavigation(NavigationService navigation) {
    this.navigationService = navigation;
  }

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new AdminDashboardViewModel(
        currentUser,
        AppConfig.userService(),
        AppConfig.bicycleService(),
        AppConfig.stationService(),
        AppConfig.rentalService(),
        AppConfig.reservationService(),
        AppConfig.bikeIssueService(),
        AppConfig.customerService()
    );
    bind();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    subtitleLabel.textProperty().bind(viewModel.subtitleText);

    totalUsersTitleLabel.textProperty().bind(viewModel.totalUsersTitle);
    totalBicyclesTitleLabel.textProperty().bind(viewModel.totalBicyclesTitle);
    activeRentalsTitleLabel.textProperty().bind(viewModel.activeRentalsTitle);
    activeReservationsTitleLabel.textProperty().bind(viewModel.activeReservationsTitle);

    totalUsersValueLabel.textProperty().bind(viewModel.totalUsersValue);
    totalBicyclesValueLabel.textProperty().bind(viewModel.totalBicyclesValue);
    activeRentalsValueLabel.textProperty().bind(viewModel.activeRentalsValue);
    activeReservationsValueLabel.textProperty().bind(viewModel.activeReservationsValue);

    attentionTitleLabel.textProperty().bind(viewModel.attentionTitle);
    latestActivityTitleLabel.textProperty().bind(viewModel.latestActivityTitle);
    quickActionsTitleLabel.textProperty().bind(viewModel.quickActionsTitle);

    needsInspectionLabel.textProperty().bind(viewModel.needsInspectionText);
    onMaintenanceLabel.textProperty().bind(viewModel.onMaintenanceText);
    unavailableLabel.textProperty().bind(viewModel.unavailableText);
    newIssuesLabel.textProperty().bind(viewModel.newIssuesText);
    totalStationsLabel.textProperty().bind(viewModel.totalStationsText);

    latestRentalLabel.textProperty().bind(viewModel.latestRentalText);
    latestReservationLabel.textProperty().bind(viewModel.latestReservationText);
    latestIssueLabel.textProperty().bind(viewModel.latestIssueText);

    openUsersButton.textProperty().bind(viewModel.openUsersButtonText);
    openBicyclesButton.textProperty().bind(viewModel.openBicyclesButtonText);
    openStationsButton.textProperty().bind(viewModel.openStationsButtonText);
  }

  @FXML
  private void onOpenUsers() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/admin/UsersManagementView.fxml");
  }

  @FXML
  private void onOpenBicycles() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/admin/BicyclesManagementView.fxml");
  }

  @FXML
  private void onOpenStations() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/admin/StationManagementView.fxml");
  }
}