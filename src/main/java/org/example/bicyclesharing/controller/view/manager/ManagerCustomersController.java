package org.example.bicyclesharing.controller.view.manager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.manager.modalController.AddEditCustomerController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.manager.ManagerCustomersViewModel;
import org.example.bicyclesharing.viewModel.manager.item.CustomerItemViewModel;

public class ManagerCustomersController extends BaseController {

  @FXML private Button addCustomerButton;
  @FXML private Label titleLabel;
  @FXML private TextField searchField;
  @FXML private Label countLabel;
  @FXML private ListView<CustomerItemViewModel> customersListView;

  private ManagerCustomersViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new ManagerCustomersViewModel(
        currentUser,
        AppConfig.customerService()
    );

    bind();
    setupFilters();
    setupList();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());
    countLabel.textProperty().bind(viewModel.countTextProperty());
    addCustomerButton.textProperty().bind(viewModel.addButtonTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
    customersListView.setItems(viewModel.getCustomers());
  }

  private void setupFilters() {
    searchField.textProperty().addListener((obs, oldVal, newVal) ->
        viewModel.applyFiltersAsync()
    );
  }

  private void setupList() {
    customersListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(CustomerItemViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        setGraphic(createCard(item));
      }
    });
  }

  private VBox createCard(CustomerItemViewModel item) {
    VBox card = new VBox(8);
    card.getStyleClass().add("user-card");

    Label fullNameLabel = new Label();
    fullNameLabel.getStyleClass().add("user-card-title");
    fullNameLabel.textProperty().bind(item.fullNameTextProperty());

    Label phoneNumberLabel = new Label();
    phoneNumberLabel.getStyleClass().add("user-card-subtitle");
    phoneNumberLabel.textProperty().bind(item.phoneNumberTextProperty());

    Label documentNumberLabel = new Label();
    documentNumberLabel.getStyleClass().add("user-card-subtitle");
    documentNumberLabel.textProperty().bind(item.documentNumberTextProperty());

    Button editButton = new Button();
    editButton.textProperty().bind(viewModel.editButtonTextProperty());
    editButton.getStyleClass().add("button-edit");
    editButton.setOnAction(e -> openCustomerDialog(item));

    Button deleteButton = new Button();
    deleteButton.textProperty().bind(viewModel.deleteButtonTextProperty());
    deleteButton.getStyleClass().add("button-danger");
    deleteButton.setOnAction(e -> deleteCustomer(item));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox actions = new HBox(10, editButton, deleteButton);
    HBox bottomRow = new HBox(10, spacer, actions);

    card.getChildren().addAll(
        fullNameLabel,
        phoneNumberLabel,
        documentNumberLabel,
        bottomRow
    );

    return card;
  }

  @FXML
  private void onAddCustomer() {
    openCustomerDialog(null);
  }

  private void openCustomerDialog(CustomerItemViewModel item) {
    try {
      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/manager/modalView/AddEditCustomerView.fxml",
          (AddEditCustomerController controller) ->
              controller.initData(
                  item == null ? null : item.getCustomer(),
                  viewModel::refreshAsync
              )
      );
    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.operation.failed"));
    }
  }

  private void deleteCustomer(CustomerItemViewModel item) {
    try {
      viewModel.deleteCustomer(item);
    } catch (BusinessException e) {
      DialogUtil.showError(e.getMessage());
    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.delete.failed"));
    }
  }
}