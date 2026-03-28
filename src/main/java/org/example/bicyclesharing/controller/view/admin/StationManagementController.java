package org.example.bicyclesharing.controller.view.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.admin.modalController.AddEditStationController;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.EmployeeService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.admin.StationManagementViewModel;

public class StationManagementController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private Label countLabel;
  @FXML private TextField searchField;
  @FXML private ListView<Station> stationsListView;
  @FXML private Button addStationButton;

  private StationManagementViewModel viewModel;
  private final EmployeeService employeeService = AppConfig.employeeService();

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new StationManagementViewModel(currentUser, AppConfig.stationService());
    bind();
    setupList();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    countLabel.textProperty().bind(viewModel.countText);
    searchField.promptTextProperty().bind(viewModel.searchPromptText);
    addStationButton.textProperty().bind(viewModel.addStationButtonText);

    searchField.textProperty().bindBidirectional(viewModel.searchText);
    searchField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.applyFilters());

    stationsListView.setItems(viewModel.getStations());
  }

  private void setupList() {
    stationsListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Station station, boolean empty) {
        super.updateItem(station, empty);

        if (empty || station == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        VBox card = new VBox(8);
        card.getStyleClass().add("user-card");

        Label nameLabel = new Label(station.getName());
        nameLabel.getStyleClass().add("user-card-title");

        Label coordsLabel = new Label(
            LocalizationManager.getStringByKey("admin.stations.coords") + ": "
                + station.getLatitude() + ", " + station.getLongitude()
        );
        coordsLabel.getStyleClass().add("user-card-subtitle");

        String employeeName = "—";

        if (station.getEmployeeId() != null) {
          var employee = employeeService.getById(station.getEmployeeId());
          if (employee != null) {
            employeeName = employee.getName();
          }
        }

        Label infoLabel = new Label(
            LocalizationManager.getStringByKey("admin.stations.employee") + ": "
                + employeeName + " | "
                + LocalizationManager.getStringByKey("admin.stations.bicycles") + ": "
                + station.getBicyclesId().size()
        );
        infoLabel.getStyleClass().add("user-card-role");

        Button editButton = new Button(LocalizationManager.getStringByKey("edit.button"));
        editButton.getStyleClass().add("button-edit");
        editButton.setOnAction(e -> openDialog(station));

        Button deleteButton = new Button(LocalizationManager.getStringByKey("admin.delete.button"));
        deleteButton.getStyleClass().add("button-danger");
        deleteButton.setOnAction(e -> viewModel.delete(station));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10, editButton, deleteButton);
        HBox bottomRow = new HBox(10, spacer, actions);

        card.getChildren().addAll(nameLabel, coordsLabel, infoLabel, bottomRow);
        setGraphic(card);
      }
    });
  }

  @FXML
  private void onAddStation() {
    openDialog(null);
  }

  private void openDialog(Station station) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/org/example/bicyclesharing/presentation/view/admin/modalView/AddEditStationView.fxml")
      );

      Parent root = loader.load();

      AddEditStationController controller = loader.getController();
      controller.initData(station, () -> {
        viewModel.load();
        viewModel.applyFilters();
      });

      Scene scene = new Scene(root);
      scene.setFill(Color.TRANSPARENT);
      scene.getStylesheets().add(
          getClass().getResource("/org/example/bicyclesharing/css/style.css").toExternalForm()
      );

      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.setScene(scene);
      stage.showAndWait();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}