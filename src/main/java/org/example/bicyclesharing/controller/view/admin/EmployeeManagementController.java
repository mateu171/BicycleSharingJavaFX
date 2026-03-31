package org.example.bicyclesharing.controller.view.admin;

import javafx.collections.FXCollections;
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
import org.example.bicyclesharing.controller.view.admin.modalController.AddEditEmployeeController;
import org.example.bicyclesharing.domain.Impl.Employee;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.admin.EmployeeManagementViewModel;

public class EmployeeManagementController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private Label countLabel;
  @FXML private TextField searchField;
  @FXML private ListView<Employee> employeesListView;
  @FXML private Button addEmployeeButton;

  private EmployeeManagementViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new EmployeeManagementViewModel(currentUser, AppConfig.employeeService());
    bind();
    setupFilters();
    setupList();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    countLabel.textProperty().bind(viewModel.countText);
    searchField.promptTextProperty().bind(viewModel.searchPromptText);
    addEmployeeButton.textProperty().bind(viewModel.addEmployeeButtonText);

    searchField.textProperty().bindBidirectional(viewModel.searchText);
    employeesListView.setItems(viewModel.getEmployees());
  }

  private void setupFilters() {
    searchField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.applyFilters());

  }

  private void setupList() {
    employeesListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Employee employee, boolean empty) {
        super.updateItem(employee, empty);

        if (empty || employee == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        VBox card = new VBox(8);
        card.getStyleClass().add("user-card");

        Label nameLabel = new Label(employee.getName());
        nameLabel.getStyleClass().add("user-card-title");

        Label phoneLabel = new Label(employee.getPhoneNumber());
        phoneLabel.getStyleClass().add("user-card-subtitle");

        Label detailsLabel = new Label(
            LocalizationManager.getStringByKey("admin.employees.salary") + ": "
                + String.format("%.2f", employee.getSalary())
        );
        detailsLabel.getStyleClass().add("user-card-role");

        Label stationLabel = new Label(
            LocalizationManager.getStringByKey("admin.employees.station") + ": " + employee.getStationId()
        );
        stationLabel.getStyleClass().add("user-card-subtitle");

        Button editButton = new Button(LocalizationManager.getStringByKey("edit.button"));
        editButton.getStyleClass().add("button-edit");
        editButton.setOnAction(e -> openDialog(employee));

        Button deleteButton = new Button(LocalizationManager.getStringByKey("admin.delete.button"));
        deleteButton.getStyleClass().add("button-danger");
        deleteButton.setOnAction(e -> viewModel.delete(employee));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10, editButton, deleteButton);
        HBox bottomRow = new HBox(10, spacer, actions);

        card.getChildren().addAll(nameLabel, phoneLabel, detailsLabel, stationLabel, bottomRow);
        setGraphic(card);
      }
    });
  }

  @FXML
  private void onAddEmployee() {
    openDialog(null);
  }

  private void openDialog(Employee employee) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/org/example/bicyclesharing/presentation/view/admin/modalView/AddEditEmployeeView.fxml")
      );

      Parent root = loader.load();

      AddEditEmployeeController controller = loader.getController();
      controller.initData(employee, () -> {
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