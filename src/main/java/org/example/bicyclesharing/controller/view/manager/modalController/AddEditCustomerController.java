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

  public void initData(Customer customer,Runnable onSaved) {
    this.viewModel = new AddEditCustomerViewModel(AppConfig.customerService(),customer);
    this.onSaved = onSaved;
    bind();

    if(viewModel.isEditMode())
    {
      fullNameField.setPromptText(customer.getFullName());
      phoneNumberField.setPromptText(customer.getPhoneNumber());
      documentNumberField.setPromptText(customer.getDocumentNumber());
    }
  }

  private void bind()
  {
    titleLabel.textProperty().bind(viewModel.titleText);
    fullNameField.textProperty().bindBidirectional(viewModel.fullName);
    fullNameLabel.textProperty().bind(viewModel.fullNameLabelText);
    fullNameErrorLabel.textProperty().bind(viewModel.fullNameError);

    phoneNumberField.textProperty().bindBidirectional(viewModel.phoneNumber);
    phoneNumberLabel.textProperty().bind(viewModel.phoneNumberLabelText);
    phoneNumberErrorLabel.textProperty().bind(viewModel.phoneNumberError);

    documentNumberField.textProperty().bindBidirectional(viewModel.documentNumber);
    documentNumberLabel.textProperty().bind(viewModel.documentNumberLabelText);
    documnetNumberErrorLabel.textProperty().bind(viewModel.documentNumberError);

    saveButton.textProperty().bind(viewModel.saveButtonText);
    cancelButton.textProperty().bind(viewModel.cancelButtonText);

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
