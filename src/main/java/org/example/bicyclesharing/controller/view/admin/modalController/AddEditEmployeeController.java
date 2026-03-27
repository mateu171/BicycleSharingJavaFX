package org.example.bicyclesharing.controller.view.admin.modalController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.Employee;
import org.example.bicyclesharing.domain.enums.EmployeeType;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.admin.modalViewModal.AddEditEmployeeViewModel;

public class AddEditEmployeeController {

  @FXML private Label titleLabel;
  @FXML private Label nameLabel;
  @FXML private Label phoneLabel;
  @FXML private Label stationLabel;
  @FXML private Label typeLabel;
  @FXML private Label salaryLabel;

  @FXML private TextField nameField;
  @FXML private TextField phoneField;
  @FXML private TextField stationField;
  @FXML private ComboBox<EmployeeType> typeComboBox;
  @FXML private TextField salaryField;

  @FXML private Label nameErrorLabel;
  @FXML private Label phoneErrorLabel;
  @FXML private Label stationErrorLabel;
  @FXML private Label typeErrorLabel;
  @FXML private Label salaryErrorLabel;

  @FXML private Button closeButton;
  @FXML private Button cancelButton;
  @FXML private Button saveButton;

  private AddEditEmployeeViewModel viewModel;
  private Runnable onSaved;

  public void initData(Employee employee, Runnable onSaved) {
    this.onSaved = onSaved;
    this.viewModel = new AddEditEmployeeViewModel(AppConfig.employeeService(), employee);
    bind();

    typeComboBox.setItems(FXCollections.observableArrayList(EmployeeType.values()));
    typeComboBox.setValue(viewModel.selectedType);

    typeComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(EmployeeType item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : LocalizationManager.getStringByKey("employee.type." + item.name()));
      }
    });

    typeComboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(EmployeeType item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : LocalizationManager.getStringByKey("employee.type." + item.name()));
      }
    });

    if (viewModel.isEditMode()) {
      nameField.setPromptText(employee.getName());
      phoneField.setPromptText(employee.getPhoneNumber());
      stationField.setPromptText(employee.getStationId().toString());
      salaryField.setPromptText(String.valueOf(employee.getSalary()));
    }
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    nameLabel.textProperty().bind(viewModel.nameLabelText);
    phoneLabel.textProperty().bind(viewModel.phoneLabelText);
    stationLabel.textProperty().bind(viewModel.stationLabelText);
    typeLabel.textProperty().bind(viewModel.typeLabelText);
    salaryLabel.textProperty().bind(viewModel.salaryLabelText);

    cancelButton.textProperty().bind(viewModel.cancelButtonText);
    saveButton.textProperty().bind(viewModel.saveButtonText);

    nameField.textProperty().bindBidirectional(viewModel.name);
    phoneField.textProperty().bindBidirectional(viewModel.phone);
    stationField.textProperty().bindBidirectional(viewModel.stationId);
    salaryField.textProperty().bindBidirectional(viewModel.salary);

    nameErrorLabel.textProperty().bind(viewModel.nameError);
    phoneErrorLabel.textProperty().bind(viewModel.phoneError);
    stationErrorLabel.textProperty().bind(viewModel.stationError);
    typeErrorLabel.textProperty().bind(viewModel.typeError);
    salaryErrorLabel.textProperty().bind(viewModel.salaryError);
  }

  @FXML
  private void onSave() {
    viewModel.selectedType = typeComboBox.getValue();
    if (viewModel.save()) {
      if (onSaved != null) onSaved.run();
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