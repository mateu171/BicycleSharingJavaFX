package org.example.bicyclesharing.controller.view.manager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.Navigable;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.NavigationService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.manager.ManagerDashboardViewModel;

public class ManagerDashboardController extends BaseController implements Navigable {

  @FXML private Label titleLabel;
  @FXML private Label subtitleLabel;

  @FXML private Label activeRentalsTitleLabel;
  @FXML private Label activeRentalsValueLabel;

  @FXML private Label activeReservationsTitleLabel;
  @FXML private Label activeReservationsValueLabel;

  @FXML private Label totalCustomersTitleLabel;
  @FXML private Label totalCustomersValueLabel;

  @FXML private Label availableBicyclesTitleLabel;
  @FXML private Label availableBicyclesValueLabel;

  @FXML private Label attentionTitleLabel;
  @FXML private Label latestActivityTitleLabel;
  @FXML private Label quickActionsTitleLabel;

  @FXML private Label issuedReservationsLabel;
  @FXML private Label newReservationsLabel;
  @FXML private Label rentedBicyclesLabel;
  @FXML private Label unavailableBicyclesLabel;
  @FXML private Label totalBicyclesLabel;

  @FXML private Label latestRentalLabel;
  @FXML private Label latestReservationLabel;
  @FXML private Label latestCustomerLabel;

  @FXML private Button openCustomersButton;
  @FXML private Button openRentalsButton;
  @FXML private Button openReservationsButton;

  private NavigationService navigationService;
  private ManagerDashboardViewModel viewModel;

  @Override
  public void setNavigation(NavigationService navigation) {
    this.navigationService = navigation;
  }

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new ManagerDashboardViewModel(
        currentUser,
        AppConfig.rentalService(),
        AppConfig.reservationService(),
        AppConfig.customerService(),
        AppConfig.bicycleService()
    );

    bind();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    subtitleLabel.textProperty().bind(viewModel.subtitleTextProperty());

    activeRentalsTitleLabel.textProperty().bind(viewModel.activeRentalsTitleProperty());
    activeReservationsTitleLabel.textProperty().bind(viewModel.activeReservationsTitleProperty());
    totalCustomersTitleLabel.textProperty().bind(viewModel.totalCustomersTitleProperty());
    availableBicyclesTitleLabel.textProperty().bind(viewModel.availableBicyclesTitleProperty());

    activeRentalsValueLabel.textProperty().bind(viewModel.activeRentalsValueProperty());
    activeReservationsValueLabel.textProperty().bind(viewModel.activeReservationsValueProperty());
    totalCustomersValueLabel.textProperty().bind(viewModel.totalCustomersValueProperty());
    availableBicyclesValueLabel.textProperty().bind(viewModel.availableBicyclesValueProperty());

    attentionTitleLabel.textProperty().bind(viewModel.attentionTitleProperty());
    latestActivityTitleLabel.textProperty().bind(viewModel.latestActivityTitleProperty());
    quickActionsTitleLabel.textProperty().bind(viewModel.quickActionsTitleProperty());

    issuedReservationsLabel.textProperty().bind(viewModel.issuedReservationsTextProperty());
    newReservationsLabel.textProperty().bind(viewModel.newReservationsTextProperty());
    rentedBicyclesLabel.textProperty().bind(viewModel.rentedBicyclesTextProperty());
    unavailableBicyclesLabel.textProperty().bind(viewModel.unavailableBicyclesTextProperty());
    totalBicyclesLabel.textProperty().bind(viewModel.totalBicyclesTextProperty());

    latestRentalLabel.textProperty().bind(viewModel.latestRentalTextProperty());
    latestReservationLabel.textProperty().bind(viewModel.latestReservationTextProperty());
    latestCustomerLabel.textProperty().bind(viewModel.latestCustomerTextProperty());

    openCustomersButton.textProperty().bind(viewModel.openCustomersButtonTextProperty());
    openRentalsButton.textProperty().bind(viewModel.openRentalsButtonTextProperty());
    openReservationsButton.textProperty().bind(viewModel.openReservationsButtonTextProperty());
  }

  @FXML
  private void onOpenCustomers() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/ManagerCustomersView.fxml");
  }

  @FXML
  private void onOpenRentals() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/ManagerActiveRentalsView.fxml");
  }

  @FXML
  private void onOpenReservations() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/ManagerReservationsView.fxml");
  }
}