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
    viewModel.loadAsync();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    subtitleLabel.textProperty().bind(viewModel.subtitleText);

    activeRentalsTitleLabel.textProperty().bind(viewModel.activeRentalsTitle);
    activeReservationsTitleLabel.textProperty().bind(viewModel.activeReservationsTitle);
    totalCustomersTitleLabel.textProperty().bind(viewModel.totalCustomersTitle);
    availableBicyclesTitleLabel.textProperty().bind(viewModel.availableBicyclesTitle);

    activeRentalsValueLabel.textProperty().bind(viewModel.activeRentalsValue);
    activeReservationsValueLabel.textProperty().bind(viewModel.activeReservationsValue);
    totalCustomersValueLabel.textProperty().bind(viewModel.totalCustomersValue);
    availableBicyclesValueLabel.textProperty().bind(viewModel.availableBicyclesValue);

    attentionTitleLabel.textProperty().bind(viewModel.attentionTitle);
    latestActivityTitleLabel.textProperty().bind(viewModel.latestActivityTitle);
    quickActionsTitleLabel.textProperty().bind(viewModel.quickActionsTitle);

    issuedReservationsLabel.textProperty().bind(viewModel.issuedReservationsText);
    newReservationsLabel.textProperty().bind(viewModel.newReservationsText);
    rentedBicyclesLabel.textProperty().bind(viewModel.rentedBicyclesText);
    unavailableBicyclesLabel.textProperty().bind(viewModel.unavailableBicyclesText);
    totalBicyclesLabel.textProperty().bind(viewModel.totalBicyclesText);

    latestRentalLabel.textProperty().bind(viewModel.latestRentalText);
    latestReservationLabel.textProperty().bind(viewModel.latestReservationText);
    latestCustomerLabel.textProperty().bind(viewModel.latestCustomerText);

    openCustomersButton.textProperty().bind(viewModel.openCustomersButtonText);
    openRentalsButton.textProperty().bind(viewModel.openRentalsButtonText);
    openReservationsButton.textProperty().bind(viewModel.openReservationsButtonText);
  }

  @FXML
  private void onOpenCustomers() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/CustomerManagementView.fxml");
  }

  @FXML
  private void onOpenRentals() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/RentalManagementView.fxml");
  }

  @FXML
  private void onOpenReservations() {
    navigationService.load("/org/example/bicyclesharing/presentation/view/manager/ReservationManagementView.fxml");
  }
}