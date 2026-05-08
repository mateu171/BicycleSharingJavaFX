package org.example.bicyclesharing.controller.view.manager.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.DocumentType;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.manager.modalViewModal.AddEditReservationViewModel;

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

  @FXML private TextField startTimeField;
  @FXML private TextField endTimeField;
  @FXML private TextField documentNumberField;
  @FXML private TextField depositAmountField;

  @FXML private Label customerErrorLabel;
  @FXML private Label bicycleErrorLabel;
  @FXML private Label startTimeErrorLabel;
  @FXML private Label endTimeErrorLabel;
  @FXML private Label documentTypeErrorLabel;
  @FXML private Label documentNumberErrorLabel;
  @FXML private Label depositAmountErrorLabel;

  @FXML private Button cancelButton;
  @FXML private Button saveButton;

  @FXML private DatePicker startDatePicker;
  @FXML private DatePicker endDatePicker;

  private Runnable onSaved;
  private AddEditReservationViewModel viewModel;

  public void initData(User currentUser, Reservation reservation, Runnable onSaved) {
    this.onSaved = onSaved;

    this.viewModel = new AddEditReservationViewModel(
        currentUser,
        AppConfig.reservationService(),
        AppConfig.customerService(),
        AppConfig.bicycleService(),
        reservation
    );
    setupConverters();
    bind();
    viewModel.initialize();
  }

  private void bind() {
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

    customerComboBox.valueProperty().bindBidirectional(viewModel.selectedCustomerProperty());
    bicycleComboBox.valueProperty().bindBidirectional(viewModel.selectedBicycleProperty());
    documentTypeComboBox.valueProperty().bindBidirectional(viewModel.selectedDocumentTypeProperty());

    startDatePicker.valueProperty().bindBidirectional(viewModel.startDateProperty());
    endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());

    startTimeField.textProperty().bindBidirectional(viewModel.startTimeProperty());
    endTimeField.textProperty().bindBidirectional(viewModel.endTimeProperty());
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
  }
  private void setupConverters() {
    customerComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Customer customer)
      {
        return customer == null ? "" : customer.getFullName();
      }

      @Override
      public Customer fromString(String string)
      {
        return null;
      }
    });

    bicycleComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Bicycle bicycle) {
        return bicycle == null ? "" : bicycle.getModel();
      }

      @Override
      public Bicycle fromString(String string) {
        return null;
      }
    });

    documentTypeComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(DocumentType documentType) {
        return documentType == null ? "" : LocalizationManager.getStringByKey(documentType.getKey());
      }

      @Override
      public DocumentType fromString(String s) {
        return null;
      }
    });
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