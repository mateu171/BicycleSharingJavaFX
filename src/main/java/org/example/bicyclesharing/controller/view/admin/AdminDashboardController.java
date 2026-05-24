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
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    subtitleLabel.textProperty().bind(viewModel.subtitleTextProperty());

    totalUsersTitleLabel.textProperty().bind(viewModel.totalUsersTitleProperty());
    totalBicyclesTitleLabel.textProperty().bind(viewModel.totalBicyclesTitleProperty());
    activeRentalsTitleLabel.textProperty().bind(viewModel.activeRentalsTitleProperty());
    activeReservationsTitleLabel.textProperty().bind(viewModel.activeReservationsTitleProperty());

    totalUsersValueLabel.textProperty().bind(viewModel.totalUsersValueProperty());
    totalBicyclesValueLabel.textProperty().bind(viewModel.totalBicyclesValueProperty());
    activeRentalsValueLabel.textProperty().bind(viewModel.activeRentalsValueProperty());
    activeReservationsValueLabel.textProperty().bind(viewModel.activeReservationsValueProperty());

    attentionTitleLabel.textProperty().bind(viewModel.attentionTitleProperty());
    latestActivityTitleLabel.textProperty().bind(viewModel.latestActivityTitleProperty());
    quickActionsTitleLabel.textProperty().bind(viewModel.quickActionsTitleProperty());

    needsInspectionLabel.textProperty().bind(viewModel.needsInspectionTextProperty());
    onMaintenanceLabel.textProperty().bind(viewModel.onMaintenanceTextProperty());
    unavailableLabel.textProperty().bind(viewModel.unavailableTextProperty());
    newIssuesLabel.textProperty().bind(viewModel.newIssuesTextProperty());
    totalStationsLabel.textProperty().bind(viewModel.totalStationsTextProperty());

    latestRentalLabel.textProperty().bind(viewModel.latestRentalTextProperty());
    latestReservationLabel.textProperty().bind(viewModel.latestReservationTextProperty());
    latestIssueLabel.textProperty().bind(viewModel.latestIssueTextProperty());

    openUsersButton.textProperty().bind(viewModel.openUsersButtonTextProperty());
    openBicyclesButton.textProperty().bind(viewModel.openBicyclesButtonTextProperty());
    openStationsButton.textProperty().bind(viewModel.openStationsButtonTextProperty());
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