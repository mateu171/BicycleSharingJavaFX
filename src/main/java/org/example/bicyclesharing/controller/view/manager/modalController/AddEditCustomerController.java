package org.example.bicyclesharing.controller.view.manager.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.manager.modalViewModal.AddEditCustomerViewModel;

public class AddEditCustomerController {

  @FXML private Label titleLabel;
  @FXML private Label fullNameLabel;
  @FXML private Label phoneNumberLabel;
  @FXML private Label documentNumberLabel;

  @FXML private TextField phoneNumberField;
  @FXML private TextField fullNameField;
  @FXML private TextField documentNumberField;

  @FXML private Label phoneNumberErrorLabel;
  @FXML private Label fullNameErrorLabel;
  @FXML private Label documnetNumberErrorLabel;

  @FXML private Button cancelButton;
  @FXML private Button saveButton;

  private Runnable onSaved;
  private AddEditCustomerViewModel viewModel;

  public void initData(Customer customer, Runnable onSaved) {
    this.onSaved = onSaved;

    viewModel = new AddEditCustomerViewModel(
        AppConfig.customerService(),
        customer
    );

    bind();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());

    fullNameLabel.textProperty().bind(viewModel.fullNameLabelTextProperty());
    phoneNumberLabel.textProperty().bind(viewModel.phoneNumberLabelTextProperty());
    documentNumberLabel.textProperty().bind(viewModel.documentNumberLabelTextProperty());

    fullNameField.textProperty().bindBidirectional(viewModel.fullNameProperty());
    phoneNumberField.textProperty().bindBidirectional(viewModel.phoneNumberProperty());
    documentNumberField.textProperty().bindBidirectional(viewModel.documentNumberProperty());

    fullNameErrorLabel.textProperty().bind(viewModel.fullNameErrorProperty());
    phoneNumberErrorLabel.textProperty().bind(viewModel.phoneNumberErrorProperty());
    documnetNumberErrorLabel.textProperty().bind(viewModel.documentNumberErrorProperty());

    saveButton.textProperty().bind(viewModel.saveButtonTextProperty());
    cancelButton.textProperty().bind(viewModel.cancelButtonTextProperty());
  }

  @FXML
  private void onSave() {
    if (viewModel.save()) {
      if (onSaved != null) {
        onSaved.run();
      }

      close();
    }
  }

  @FXML
  private void onClose() {
    close();
  }

  private void close() {
    ((Stage) saveButton.getScene().getWindow()).close();
  }
}