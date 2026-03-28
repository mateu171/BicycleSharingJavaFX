package org.example.bicyclesharing.controller.view.admin.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.Employee;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.admin.modalViewModal.AddEditStationViewModel;

public class AddEditStationController {

  @FXML private Label titleLabel;
  @FXML private Label nameLabel;
  @FXML private Label latitudeLabel;
  @FXML private Label longitudeLabel;
  @FXML private Label employeeLabel;

  @FXML private TextField nameField;
  @FXML private TextField latitudeField;
  @FXML private TextField longitudeField;
  @FXML private ComboBox<Employee> employeeComboBox;

  @FXML private Label nameErrorLabel;
  @FXML private Label latitudeErrorLabel;
  @FXML private Label longitudeErrorLabel;
  @FXML private Label employeeErrorLabel;

  @FXML private Button closeButton;
  @FXML private Button cancelButton;
  @FXML private Button saveButton;

  private AddEditStationViewModel viewModel;
  private Runnable onSaved;

  public void initData(Station station, Runnable onSaved) {
    this.onSaved = onSaved;
    this.viewModel = new AddEditStationViewModel(
        AppConfig.stationService(),
        AppConfig.employeeService(),
        station
    );

    bind();

    employeeComboBox.setItems(viewModel.employees);

    if (!viewModel.employees.isEmpty()) {
      employeeComboBox.setValue(viewModel.selectedEmployee);
    }

    employeeComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(Employee item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getName());
      }
    });

    employeeComboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(Employee item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getName());
      }
    });

    if (viewModel.isEditMode()) {
      nameField.setPromptText(station.getName());
      latitudeField.setPromptText(String.valueOf(station.getLatitude()));
      longitudeField.setPromptText(String.valueOf(station.getLongitude()));
    }
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    nameLabel.textProperty().bind(viewModel.nameLabelText);
    latitudeLabel.textProperty().bind(viewModel.latitudeLabelText);
    longitudeLabel.textProperty().bind(viewModel.longitudeLabelText);
    employeeLabel.textProperty().bind(viewModel.employeeLabelText);

    cancelButton.textProperty().bind(viewModel.cancelButtonText);
    saveButton.textProperty().bind(viewModel.saveButtonText);

    nameField.textProperty().bindBidirectional(viewModel.name);
    latitudeField.textProperty().bindBidirectional(viewModel.latitude);
    longitudeField.textProperty().bindBidirectional(viewModel.longitude);

    nameErrorLabel.textProperty().bind(viewModel.nameError);
    latitudeErrorLabel.textProperty().bind(viewModel.latitudeError);
    longitudeErrorLabel.textProperty().bind(viewModel.longitudeError);
    employeeErrorLabel.textProperty().bind(viewModel.employeeError);
  }

  @FXML
  private void onSave() {
    viewModel.selectedEmployee = employeeComboBox.getValue();

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