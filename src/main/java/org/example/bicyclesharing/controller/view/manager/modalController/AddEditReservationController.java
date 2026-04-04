package org.example.bicyclesharing.controller.view.manager.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

  private Runnable onSaved;
  private AddEditReservationViewModel viewModel;

  public void initData(User currentUser, Reservation reservation, Runnable onSaved) {
    this.viewModel = new AddEditReservationViewModel(
        currentUser,
        AppConfig.reservationService(),
        AppConfig.customerService(),
        AppConfig.bicycleService(),
        reservation
    );
    this.onSaved = onSaved;
    bind();
    setupCombos();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);

    customerLabel.textProperty().bind(viewModel.customerLabelText);
    bicycleLabel.textProperty().bind(viewModel.bicycleLabelText);
    startTimeLabel.textProperty().bind(viewModel.startTimeLabelText);
    endTimeLabel.textProperty().bind(viewModel.endTimeLabelText);
    documentTypeLabel.textProperty().bind(viewModel.documentTypeLabelText);
    documentNumberLabel.textProperty().bind(viewModel.documentNumberLabelText);
    depositAmountLabel.textProperty().bind(viewModel.depositAmountLabelText);

    startTimeField.textProperty().bindBidirectional(viewModel.startTime);
    endTimeField.textProperty().bindBidirectional(viewModel.endTime);
    documentNumberField.textProperty().bindBidirectional(viewModel.documentNumber);
    depositAmountField.textProperty().bindBidirectional(viewModel.depositAmount);

    customerErrorLabel.textProperty().bind(viewModel.customerError);
    bicycleErrorLabel.textProperty().bind(viewModel.bicycleError);
    startTimeErrorLabel.textProperty().bind(viewModel.startTimeError);
    endTimeErrorLabel.textProperty().bind(viewModel.endTimeError);
    documentTypeErrorLabel.textProperty().bind(viewModel.documentTypeError);
    documentNumberErrorLabel.textProperty().bind(viewModel.documentNumberError);
    depositAmountErrorLabel.textProperty().bind(viewModel.depositAmountError);

    saveButton.textProperty().bind(viewModel.saveButtonText);
    cancelButton.textProperty().bind(viewModel.cancelButtonText);

    startTimeField.setPromptText("2026-04-04T10:00");
    endTimeField.setPromptText("2026-04-04T12:00");
  }

  private void setupCombos() {
    customerComboBox.setItems(viewModel.getCustomers());
    bicycleComboBox.setItems(viewModel.getBicycles());
    documentTypeComboBox.setItems(viewModel.getDocumentTypes());

    customerComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Customer customer) {
        return customer == null ? "" : customer.getFullName();
      }

      @Override
      public Customer fromString(String string) {
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
      public String toString(DocumentType type) {
        return type == null ? "" : LocalizationManager.getStringByKey(type.getKey());
      }

      @Override
      public DocumentType fromString(String string) {
        return null;
      }
    });

    customerComboBox.getSelectionModel().select(viewModel.getSelectedCustomer());
    bicycleComboBox.getSelectionModel().select(viewModel.getSelectedBicycle());
    documentTypeComboBox.getSelectionModel().select(viewModel.getSelectedDocumentType());

    customerComboBox.valueProperty().addListener((obs, oldVal, newVal) -> viewModel.setSelectedCustomer(newVal));
    bicycleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> viewModel.setSelectedBicycle(newVal));
    documentTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> viewModel.setSelectedDocumentType(newVal));

    startTimeField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.startTimeError.set(""));
    endTimeField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.endTimeError.set(""));
    documentNumberField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.documentNumberError.set(""));
    depositAmountField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.depositAmountError.set(""));
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