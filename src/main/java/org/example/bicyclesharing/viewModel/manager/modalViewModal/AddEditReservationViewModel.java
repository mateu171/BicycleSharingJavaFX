package org.example.bicyclesharing.viewModel.manager.modalViewModal;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.DocumentType;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.services.ReservationService;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditReservationViewModel {

  private final ReservationService reservationService;
  private final CustomerService customerService;
  private final BicycleService bicycleService;
  private final Reservation editingReservation;
  private final User currentUser;

  private final ObservableList<Customer> customers = FXCollections.observableArrayList();
  private final ObservableList<Bicycle> bicycles = FXCollections.observableArrayList();
  private final ObservableList<DocumentType> documentTypes = FXCollections.observableArrayList();

  private final StringProperty titleText = new SimpleStringProperty();

  private final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");

  private final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  private final StringProperty customerLabelText =
      LocalizationManager.getStringProperty("manager.reservation.customer");

  private final StringProperty bicycleLabelText =
      LocalizationManager.getStringProperty("manager.reservation.bicycle");

  private final StringProperty startTimeLabelText =
      LocalizationManager.getStringProperty("manager.reservation.start");

  private final StringProperty endTimeLabelText =
      LocalizationManager.getStringProperty("manager.reservation.end");

  private final StringProperty documentTypeLabelText =
      LocalizationManager.getStringProperty("manager.reservation.document.type");

  private final StringProperty documentNumberLabelText =
      LocalizationManager.getStringProperty("manager.reservation.document.number");

  private final StringProperty depositAmountLabelText =
      LocalizationManager.getStringProperty("manager.reservation.deposit");

  private final ObjectProperty<Customer> selectedCustomer = new SimpleObjectProperty<>();
  private final ObjectProperty<Bicycle> selectedBicycle = new SimpleObjectProperty<>();
  private final ObjectProperty<DocumentType> selectedDocumentType = new SimpleObjectProperty<>();

  private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
  private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

  private final StringProperty startTime = new SimpleStringProperty("");
  private final StringProperty endTime = new SimpleStringProperty("");
  private final StringProperty documentNumber = new SimpleStringProperty("");
  private final StringProperty depositAmount = new SimpleStringProperty("");

  private final StringProperty customerError = new SimpleStringProperty("");
  private final StringProperty bicycleError = new SimpleStringProperty("");
  private final StringProperty startTimeError = new SimpleStringProperty("");
  private final StringProperty endTimeError = new SimpleStringProperty("");
  private final StringProperty documentTypeError = new SimpleStringProperty("");
  private final StringProperty documentNumberError = new SimpleStringProperty("");
  private final StringProperty depositAmountError = new SimpleStringProperty("");

  public AddEditReservationViewModel(
      User currentUser,
      ReservationService reservationService,
      CustomerService customerService,
      BicycleService bicycleService,
      Reservation editingReservation) {

    this.currentUser = currentUser;
    this.reservationService = reservationService;
    this.customerService = customerService;
    this.bicycleService = bicycleService;
    this.editingReservation = editingReservation;
  }

  public void initialize()
  {
    loadOptions();

    if(isEditMode())
      initializeEditMode();
    else
      initializeAddMode();
  }

  private void initializeAddMode() {
    titleText.set(LocalizationManager.getStringByKey("manager.reservation.add.title"));
  }

  private void initializeEditMode() {
    titleText.set(LocalizationManager.getStringByKey("manager.reservation.edit.title"));
    startDate.set(editingReservation.getStartTime().toLocalDate());
    startTime.set(editingReservation.getStartTime().toLocalTime().toString());

    endDate.set(editingReservation.getEndTime().toLocalDate());
    endTime.set(editingReservation.getEndTime().toLocalTime().toString());

    documentNumber.set(editingReservation.getDocumentNumber());
    depositAmount.set(String.valueOf(editingReservation.getDepositAmount()));

    Customer customer = customerService.getById(editingReservation.getCustomerId()).orElse(null);
    Bicycle bicycle = bicycleService.getById(editingReservation.getBicycleId()).orElse(null);

    if (customer != null && !customers.contains(customer)) {
      customers.add(customer);
    }

    if (bicycle != null && !bicycles.contains(bicycle)) {
      bicycles.add(bicycle);
    }

    selectedCustomer.set(customer);
    selectedBicycle.set(bicycle);
    selectedDocumentType.set(editingReservation.getDocumentType());
  }

  private void loadOptions() {
    customers.setAll(customerService.getAll().stream()
        .filter(c -> c.getActiveRent() == null)
        .collect(Collectors.toList()));

    bicycles.setAll(bicycleService.getAvailable());
    documentTypes.setAll(DocumentType.values());
  }

  public boolean save() {
    clearErrors();

    try {
      if(isEditMode()) {
        updateReservation();
      }
      else {
        createReservation();
      }
      return  true;
    } catch (CustomEntityValidationExeption e) {
     applyValidationErrors(e);
      return false;
    }
  }

  private void applyValidationErrors(CustomEntityValidationExeption e)
  {
    e.getErrors().forEach((field, messages) -> {
      String text = messages.stream()
          .map(LocalizationManager::getStringByKey)
          .collect(Collectors.joining("\n"));

      switch (field) {
        case "customerId" -> customerError.set(text);
        case "bicycleId" -> bicycleError.set(text);
        case "startTime" -> startTimeError.set(text);
        case "endTime" -> endTimeError.set(text);
        case "documentType" -> documentTypeError.set(text);
        case "documentNumber" -> documentNumberError.set(text);
        case "depositAmount" -> depositAmountError.set(text);
      }
    });
  }

  private void createReservation() {
    Reservation reservation = buildReservation();

    if(selectedCustomer.get() != null)
    {
      selectedCustomer.get().setActiveReservation(reservation.getId());
    }

    reservationService.add(reservation);

    if(selectedCustomer.get() != null)
    {
      customerService.update(selectedCustomer.get());
    }
  }

  private void updateReservation() {
    Reservation validated = buildReservation();

    editingReservation.setCustomerId(validated.getCustomerId());
    editingReservation.setBicycleId(validated.getBicycleId());
    editingReservation.setManagerId(validated.getManagerId());
    editingReservation.setStartTime(validated.getStartTime().toString());
    editingReservation.setEndTime(validated.getEndTime().toString());
    editingReservation.setDocumentType(validated.getDocumentType());
    editingReservation.setDocumentNumber(validated.getDocumentNumber());
    editingReservation.setDepositAmount(String.valueOf(validated.getDepositAmount()));

    reservationService.update(editingReservation);
  }

  private Reservation buildReservation()
  {
    return new Reservation(
        selectedCustomer.get() == null ? null : selectedCustomer.get().getId(),
        selectedBicycle.get() == null ? null : selectedBicycle.get().getId(),
        currentUser.getId(),
        buildDateTime(startDate.get(),startTime.get()),
        buildDateTime(endDate.get(),endTime.get()),
        selectedDocumentType.get(),
        documentNumber.get(),
        depositAmount.get()
    );
  }

  private String buildDateTime(LocalDate date,String timeText)
  {
    String time = timeText == null ? "" : timeText.trim();

    if(date == null || time.isBlank())
    {
      return  "";
    }

    return date + "T" + time;
  }

  public boolean isEditMode() {
    return editingReservation != null;
  }

  private void clearErrors() {
    customerError.set("");
    bicycleError.set("");
    startTimeError.set("");
    endTimeError.set("");
    documentTypeError.set("");
    documentNumberError.set("");
    depositAmountError.set("");
  }

  public ObservableList<Customer> getCustomers() {
    return customers;
  }

  public ObservableList<Bicycle> getBicycles() {
    return bicycles;
  }

  public ObservableList<DocumentType> getDocumentTypes() {
    return documentTypes;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty saveButtonTextProperty() {
    return saveButtonText;
  }

  public StringProperty cancelButtonTextProperty() {
    return cancelButtonText;
  }

  public StringProperty customerLabelTextProperty() {
    return customerLabelText;
  }

  public StringProperty bicycleLabelTextProperty() {
    return bicycleLabelText;
  }

  public StringProperty startTimeLabelTextProperty() {
    return startTimeLabelText;
  }

  public StringProperty endTimeLabelTextProperty() {
    return endTimeLabelText;
  }

  public StringProperty documentTypeLabelTextProperty() {
    return documentTypeLabelText;
  }

  public StringProperty documentNumberLabelTextProperty() {
    return documentNumberLabelText;
  }

  public StringProperty depositAmountLabelTextProperty() {
    return depositAmountLabelText;
  }

  public ObjectProperty<Customer> selectedCustomerProperty() {
    return selectedCustomer;
  }

  public ObjectProperty<Bicycle> selectedBicycleProperty() {
    return selectedBicycle;
  }

  public ObjectProperty<DocumentType> selectedDocumentTypeProperty() {
    return selectedDocumentType;
  }

  public ObjectProperty<LocalDate> startDateProperty() {
    return startDate;
  }

  public ObjectProperty<LocalDate> endDateProperty() {
    return endDate;
  }

  public StringProperty startTimeProperty() {
    return startTime;
  }

  public StringProperty endTimeProperty() {
    return endTime;
  }

  public StringProperty documentNumberProperty() {
    return documentNumber;
  }

  public StringProperty depositAmountProperty() {
    return depositAmount;
  }

  public StringProperty customerErrorProperty() {
    return customerError;
  }

  public StringProperty bicycleErrorProperty() {
    return bicycleError;
  }

  public StringProperty startTimeErrorProperty() {
    return startTimeError;
  }

  public StringProperty endTimeErrorProperty() {
    return endTimeError;
  }

  public StringProperty documentTypeErrorProperty() {
    return documentTypeError;
  }

  public StringProperty documentNumberErrorProperty() {
    return documentNumberError;
  }

  public StringProperty depositAmountErrorProperty() {
    return depositAmountError;
  }
}