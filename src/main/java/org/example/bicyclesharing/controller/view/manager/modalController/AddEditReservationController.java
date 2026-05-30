package org.example.bicyclesharing.controller.view.manager.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.bicyclesharing.domain.Impl.*;
import org.example.bicyclesharing.domain.enums.DocumentType;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.manager.modalViewModal.AddEditReservationViewModel;

import java.time.LocalDate;

public class AddEditReservationController {

  @FXML private Label titleLabel;
  @FXML private Label customerLabel;
  @FXML private Label bicycleLabel;
  @FXML private Label startTimeLabel;
  @FXML private Label endTimeLabel;
  @FXML private Label documentTypeLabel;
  @FXML private Label documentNumberLabel;
  @FXML private Label depositAmountLabel;
  @FXML private ComboBox<Customer> customerComboBox;
  @FXML private ComboBox<Bicycle> bicycleComboBox;
  @FXML private ComboBox<DocumentType> documentTypeComboBox;

  @FXML private ComboBox<String> startHourComboBox;
  @FXML private ComboBox<String> endHourComboBox;

  @FXML private DatePicker startDatePicker;
  @FXML private DatePicker endDatePicker;

  @FXML private TextField documentNumberField;
  @FXML private TextField depositAmountField;

  @FXML private Label customerErrorLabel;
  @FXML private Label bicycleErrorLabel;
  @FXML private Label startTimeErrorLabel;
  @FXML private Label endTimeErrorLabel;
  @FXML private Label documentTypeErrorLabel;
  @FXML private Label documentNumberErrorLabel;
  @FXML private Label depositAmountErrorLabel;

  @FXML private Button saveButton;
  @FXML private Button cancelButton;

  private AddEditReservationViewModel viewModel;
  private Runnable onSaved;

  public void initData(User user, Reservation reservation, Runnable onSaved) {

    this.onSaved = onSaved;

    this.viewModel = new AddEditReservationViewModel(
        user,
        AppConfig.reservationService(),
        AppConfig.customerService(),
        AppConfig.bicycleService(),
        AppConfig.stationService(),
        reservation
    );

    setup();
    viewModel.initialize();
  }

  private void setup() {

    titleLabel.textProperty().bind(viewModel.titleTextProperty());

    customerLabel.textProperty().bind(viewModel.customerLabelTextProperty());
    bicycleLabel.textProperty().bind(viewModel.bicycleLabelTextProperty());
    startTimeLabel.textProperty().bind(viewModel.startTimeLabelTextProperty());
    endTimeLabel.textProperty().bind(viewModel.endTimeLabelTextProperty());
    documentTypeLabel.textProperty().bind(viewModel.documentTypeLabelTextProperty());
    documentNumberLabel.textProperty().bind(viewModel.documentNumberLabelTextProperty());
    depositAmountLabel.textProperty().bind(viewModel.depositAmountLabelTextProperty());

    customerComboBox.setItems(viewModel.getCustomers());
    bicycleComboBox.setItems(viewModel.getBicycles());
    documentTypeComboBox.setItems(viewModel.getDocumentTypes());

    startHourComboBox.setItems(viewModel.getAvailableStartHours());
    endHourComboBox.setItems(viewModel.getAvailableEndHours());

    customerComboBox.valueProperty().bindBidirectional(viewModel.selectedCustomerProperty());
    bicycleComboBox.valueProperty().bindBidirectional(viewModel.selectedBicycleProperty());
    documentTypeComboBox.valueProperty().bindBidirectional(viewModel.selectedDocumentTypeProperty());

    startDatePicker.valueProperty().bindBidirectional(viewModel.startDateProperty());
    endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

    startHourComboBox.valueProperty().bindBidirectional(viewModel.startTimeProperty());
    endHourComboBox.valueProperty().bindBidirectional(viewModel.endTimeProperty());

    documentNumberField.textProperty().bindBidirectional(viewModel.documentNumberProperty());
    depositAmountField.textProperty().bindBidirectional(viewModel.depositAmountProperty());

    customerErrorLabel.textProperty().bind(viewModel.customerErrorProperty());
    bicycleErrorLabel.textProperty().bind(viewModel.bicycleErrorProperty());
    startTimeErrorLabel.textProperty().bind(viewModel.startTimeErrorProperty());
    endTimeErrorLabel.textProperty().bind(viewModel.endTimeErrorProperty());
    documentTypeErrorLabel.textProperty().bind(viewModel.documentTypeErrorProperty());
    documentNumberErrorLabel.textProperty().bind(viewModel.documentNumberErrorProperty());
    depositAmountErrorLabel.textProperty().bind(viewModel.depositAmountErrorProperty());

    saveButton.textProperty().bind(viewModel.saveButtonTextProperty());
    cancelButton.textProperty().bind(viewModel.cancelButtonTextProperty());

    setupConverters();
    setupDateBlocking();
  }

  private void setupDateBlocking() {

    startDatePicker.setDayCellFactory(p -> new DateCell() {
      @Override
      public void updateItem(LocalDate d, boolean empty) {
        super.updateItem(d, empty);
        setDisable(empty || d.isBefore(LocalDate.now()));
      }
    });

    endDatePicker.setDayCellFactory(p -> new DateCell() {
      @Override
      public void updateItem(LocalDate d, boolean empty) {
        super.updateItem(d, empty);
        setDisable(empty || d.isBefore(LocalDate.now()));
      }
    });
  }

  private void setupConverters() {

    customerComboBox.setConverter(new StringConverter<>() {
      public String toString(Customer c) {
        return c == null ? "" : c.getFullName();
      }
      public Customer fromString(String s) { return null; }
    });

    bicycleComboBox.setConverter(new StringConverter<>() {
      public String toString(Bicycle b) {
        return b == null ? "" : b.getModel();
      }
      public Bicycle fromString(String s) { return null; }
    });
  }

  @FXML
  private void onSave() {
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