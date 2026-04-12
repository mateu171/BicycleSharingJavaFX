package org.example.bicyclesharing.controller.view.manager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.manager.modalController.AddEditCustomerController;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.viewModel.manager.ManagerCustomersViewModel;

public class ManagerCustomersController extends BaseController {

  @FXML private Button addCustomerButton;
  @FXML private Label titleLabel;
  @FXML private TextField searchField;
  @FXML private Label countLabel;
  @FXML private ListView<Customer> customersListView;

  private ManagerCustomersViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new ManagerCustomersViewModel(currentUser, AppConfig.customerService());
    binds();
    setupFilters();
    setupList();
  }

  private void setupFilters() {
    searchField.textProperty().addListener((obs, oldVal, newVal) -> {
      viewModel.applyFilters();
    });
  }

  private void binds()
  {
    titleLabel.textProperty().bind(viewModel.titleText);
    searchField.promptTextProperty().bind(viewModel.searchPromptText);
    countLabel.textProperty().bind(viewModel.countText);
    searchField.textProperty().bindBidirectional(viewModel.searchText);
    customersListView.setItems(viewModel.getCustomers());
    addCustomerButton.textProperty().bind(viewModel.addButtonText);
  }

  private void setupList() {
    customersListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Customer customer, boolean empty) {
        super.updateItem(customer, empty);

        if (empty || customer == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        VBox card = new VBox(8);
        card.getStyleClass().add("user-card");

        Label fullNameLabel = new Label(customer.getFullName());
        fullNameLabel.getStyleClass().add("user-card-title");

        Label phoneNumberLabel = new Label(customer.getPhoneNumber());
        phoneNumberLabel.getStyleClass().add("user-card-subtitle");

        Label documentNumberLabel = new Label(customer.getDocumentNumber());
        documentNumberLabel.getStyleClass().add("user-card-subtitle");

        Button editButton = new Button();
        editButton.textProperty().bind(viewModel.editButtonText);
        editButton.getStyleClass().add("button-edit");
        editButton.setOnAction(e -> {
          openCustomerDialog(customer);
        });

        Button deleteButton = new Button();
        deleteButton.textProperty().bind(viewModel.deleteButtonText);
        deleteButton.getStyleClass().add("button-danger");
        deleteButton.setOnAction(e -> {
          try {
            viewModel.deleteCustomer(customer);
          }catch (BusinessException ex)
          {
            DialogUtil.showError(ex.getMessage());
          }catch (Exception ex) {
            DialogUtil.showError("error.delete.failed");
          }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10, editButton,deleteButton);
        HBox bottomRow = new HBox(10, spacer, actions);

        card.getChildren().addAll(fullNameLabel, phoneNumberLabel,documentNumberLabel,bottomRow);
        setGraphic(card);
      }
    });
  }

  @FXML
  private void onAddCustomer()
  {
    openCustomerDialog(null);
  }

  private void openCustomerDialog(Customer customer) {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource(
              "/org/example/bicyclesharing/presentation/view/manager/modalView/AddEditCustomerView.fxml")
      );

      Parent root = loader.load();

      AddEditCustomerController controller = loader.getController();
      controller.initData(customer, () -> {
        viewModel.loadCustomers();
        viewModel.applyFilters();
      });

      Scene scene = new Scene(root);
      scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
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