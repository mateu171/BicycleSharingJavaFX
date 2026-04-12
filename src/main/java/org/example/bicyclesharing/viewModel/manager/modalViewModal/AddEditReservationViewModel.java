package org.example.bicyclesharing.viewModel.manager.modalViewModal;

import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.Reservation;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.DocumentType;
import org.example.bicyclesharing.domain.enums.StateBicycle;
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

  public final StringProperty titleText = new SimpleStringProperty();

  public final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  public final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  public final StringProperty customerLabelText =
      LocalizationManager.getStringProperty("manager.reservation.customer");
  public final StringProperty bicycleLabelText =
      LocalizationManager.getStringProperty("manager.reservation.bicycle");
  public final StringProperty startTimeLabelText =
      LocalizationManager.getStringProperty("manager.reservation.start");
  public final StringProperty endTimeLabelText =
      LocalizationManager.getStringProperty("manager.reservation.end");
  public final StringProperty documentTypeLabelText =
      LocalizationManager.getStringProperty("manager.reservation.document.type");
  public final StringProperty documentNumberLabelText =
      LocalizationManager.getStringProperty("manager.reservation.document.number");
  public final StringProperty depositAmountLabelText =
      LocalizationManager.getStringProperty("manager.reservation.deposit");

  public final StringProperty startTime = new SimpleStringProperty("");
  public final StringProperty endTime = new SimpleStringProperty("");
  public final StringProperty documentNumber = new SimpleStringProperty("");
  public final StringProperty depositAmount = new SimpleStringProperty("");

  public final StringProperty customerError = new SimpleStringProperty("");
  public final StringProperty bicycleError = new SimpleStringProperty("");
  public final StringProperty startTimeError = new SimpleStringProperty("");
  public final StringProperty endTimeError = new SimpleStringProperty("");
  public final StringProperty documentTypeError = new SimpleStringProperty("");
  public final StringProperty documentNumberError = new SimpleStringProperty("");
  public final StringProperty depositAmountError = new SimpleStringProperty("");

  private Customer selectedCustomer;
  private Bicycle selectedBicycle;
  private DocumentType selectedDocumentType;

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

    customers.setAll(customerService.getAll().stream()
        .filter(c -> c.getActiveRent() == null)
        .collect(Collectors.toList()));

    bicycles.setAll(bicycleService.getAvailable());
    documentTypes.setAll(DocumentType.values());

    if (editingReservation == null) {
      titleText.set(LocalizationManager.getStringByKey("manager.reservation.add.title"));
    } else {
      titleText.set(LocalizationManager.getStringByKey("manager.reservation.edit.title"));
      startTime.set(editingReservation.getStartTime().toString());
      endTime.set(editingReservation.getEndTime().toString());
      documentNumber.set(editingReservation.getDocumentNumber());
      depositAmount.set(String.valueOf(editingReservation.getDepositAmount()));

      selectedCustomer = customerService.getById(editingReservation.getCustomerId()).orElse(null);
      selectedBicycle = bicycleService.getById(editingReservation.getBicycleId()).orElse(null);
      selectedDocumentType = editingReservation.getDocumentType();
    }
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

  public Customer getSelectedCustomer() {
    return selectedCustomer;
  }

  public void setSelectedCustomer(Customer selectedCustomer) {
    this.selectedCustomer = selectedCustomer;
    customerError.set("");
  }

  public Bicycle getSelectedBicycle() {
    return selectedBicycle;
  }

  public void setSelectedBicycle(Bicycle selectedBicycle) {
    this.selectedBicycle = selectedBicycle;
    bicycleError.set("");
  }

  public DocumentType getSelectedDocumentType() {
    return selectedDocumentType;
  }

  public void setSelectedDocumentType(DocumentType selectedDocumentType) {
    this.selectedDocumentType = selectedDocumentType;
    documentTypeError.set("");
  }

  public boolean save() {
    clearErrors();

    try {
      if (editingReservation == null) {
        Reservation reservation = new Reservation(
            selectedCustomer == null ? null : selectedCustomer.getId(),
            selectedBicycle == null ? null : selectedBicycle.getId(),
            currentUser.getId(),
            startTime.get(),
            endTime.get(),
            selectedDocumentType == null ? null : selectedDocumentType,
            documentNumber.get(),
            depositAmount.get()
        );
        selectedCustomer.setActiveReservation(reservation.getId());
        reservationService.add(reservation);
        customerService.update(selectedCustomer);
      } else {

        List<Bicycle> available = bicycleService.getAvailable();

        if (editingReservation != null) {
          Bicycle current = bicycleService.getById(editingReservation.getBicycleId()).orElse(null);
          if (current != null && !available.contains(current)) {
            available.add(current);
          }
        }
        bicycles.setAll(available);
        Reservation validated = new Reservation(
            selectedCustomer.getId(),
            selectedBicycle.getId(),
            currentUser.getId(),
            startTime.get(),
            endTime.get(),
            selectedDocumentType,
            documentNumber.get(),
            depositAmount.get()
        );

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
      return true;

    } catch (CustomEntityValidationExeption e) {
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
      return false;
    }
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
}