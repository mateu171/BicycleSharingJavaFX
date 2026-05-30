package org.example.bicyclesharing.viewModel.manager.modalViewModal;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.*;
import org.example.bicyclesharing.domain.enums.DocumentType;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.*;
import org.example.bicyclesharing.util.LocalizationManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AddEditReservationViewModel {

  private final ReservationService reservationService;
  private final CustomerService customerService;
  private final BicycleService bicycleService;
  private final StationService stationService;
  private final Reservation editingReservation;
  private final User currentUser;

  private final ObservableList<Customer> customers = FXCollections.observableArrayList();
  private final ObservableList<Bicycle> bicycles = FXCollections.observableArrayList();
  private final ObservableList<DocumentType> documentTypes = FXCollections.observableArrayList();

  private final ObservableList<String> allHours = FXCollections.observableArrayList();
  private final ObservableList<String> availableStartHours = FXCollections.observableArrayList();
  private final ObservableList<String> availableEndHours = FXCollections.observableArrayList();

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
  private final StringProperty titleText = new SimpleStringProperty();

  public AddEditReservationViewModel(
      User currentUser,
      ReservationService reservationService,
      CustomerService customerService,
      BicycleService bicycleService,
      StationService stationService,
      Reservation editingReservation
  ) {
    this.currentUser = currentUser;
    this.reservationService = reservationService;
    this.customerService = customerService;
    this.bicycleService = bicycleService;
    this.stationService = stationService;
    this.editingReservation = editingReservation;
  }

  public void initialize() {
    loadData();
    initHours();
    setupListeners();

    titleText.set(isEditMode()
        ? LocalizationManager.getStringByKey("manager.reservation.edit.title")
        : LocalizationManager.getStringByKey("manager.reservation.add.title"));

    refreshHours();
  }

  private void loadData() {

    Station station = stationService.getByManagerId(currentUser.getId());

    if (station != null) {
      bicycles.setAll(
          bicycleService.getAvailableByStation(station.getId())
      );
    } else {
      bicycles.clear();
    }

    customers.setAll(customerService.getAll());
    documentTypes.setAll(DocumentType.values());
  }

  private void initHours() {
    allHours.setAll(
        IntStream.range(0, 24)
            .mapToObj(i -> String.format("%02d:00", i))
            .collect(Collectors.toList())
    );

    availableStartHours.setAll(allHours);
    availableEndHours.setAll(allHours);
  }

  private void setupListeners() {

    selectedBicycle.addListener((o, a, b) -> refreshHours());
    startDate.addListener((o, a, b) -> refreshHours());
    endDate.addListener((o, a, b) -> refreshHours());
    startTime.addListener((o, a, b) -> updateEndHours());
  }

  private void updateEndHours() {

    if (selectedBicycle.get() == null
        || startDate.get() == null
        || startTime.get() == null
        || startTime.get().isBlank()) {

      availableEndHours.setAll(allHours);
      return;
    }

    List<String> blocked = reservationService
        .getByBicycleId(selectedBicycle.get().getId())
        .stream()
        .filter(this::isSameDate)
        .flatMap(r -> extractHours(r).stream())
        .distinct()
        .sorted()
        .toList();

    String firstBlockedAfterStart = blocked.stream()
        .filter(h -> h.compareTo(startTime.get()) > 0)
        .findFirst()
        .orElse(null);

    availableEndHours.setAll(
        allHours.stream()
            .filter(h -> h.compareTo(startTime.get()) > 0)
            .filter(h ->
                firstBlockedAfterStart == null
                    || h.compareTo(firstBlockedAfterStart) <= 0
            )
            .toList()
    );
  }

  public boolean save() {
    clearErrors();

    try {
      Reservation r = build();

      if (isEditMode()) reservationService.update(r);
      else reservationService.add(r);

      return true;

    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;
    }
  }

  private Reservation build() {
    return new Reservation(
        selectedCustomer.get() == null ? null : selectedCustomer.get().getId(),
        selectedBicycle.get() == null ? null : selectedBicycle.get().getId(),
        currentUser.getId(),
        startDate.get() + "T" + startTime.get(),
        endDate.get() + "T" + endTime.get(),
        selectedDocumentType.get(),
        documentNumber.get(),
        depositAmount.get()
    );
  }

  private void clearErrors() {
    customerError.set("");
    bicycleError.set("");
    startTimeError.set("");
    endTimeError.set("");
  }

  public boolean isEditMode() {
    return editingReservation != null;
  }

  private void applyValidationErrors(CustomEntityValidationExeption e) {
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

  private void refreshHours() {

    if (selectedBicycle.get() == null || startDate.get() == null) {
      availableStartHours.setAll(allHours);
      updateEndHours();
      return;
    }

    List<String> blocked = reservationService
        .getByBicycleId(selectedBicycle.get().getId())
        .stream()
        .filter(this::isSameDate)
        .flatMap(r -> extractHours(r).stream())
        .distinct()
        .toList();

    List<String> result = allHours.stream()
        .filter(h -> !blocked.contains(h))
        .collect(Collectors.toList());

    if (startDate.get() != null && startDate.get().isEqual(LocalDate.now())) {

      String now = String.format("%02d:00", LocalTime.now().getHour());

      result = result.stream()
          .filter(h -> h.compareTo(now) > 0)
          .toList();
    }

    availableStartHours.setAll(result);
    updateEndHours();
  }

  public boolean isSameDate(Reservation r) {
    return r.getStartTime().toLocalDate().equals(startDate.get());
  }

  public List<String> extractHours(Reservation r) {

    LocalTime start = r.getStartTime().toLocalTime();
    LocalTime end = r.getEndTime().toLocalTime();

    return IntStream
        .range(start.getHour(), end.getHour() + 1)
        .mapToObj(i -> String.format("%02d:00", i))
        .toList();
  }

  public ObservableList<Customer> getCustomers() { return customers; }
  public ObservableList<Bicycle> getBicycles() { return bicycles; }
  public ObservableList<DocumentType> getDocumentTypes() { return documentTypes; }

  public ObservableList<String> getAvailableStartHours() { return availableStartHours; }
  public ObservableList<String> getAvailableEndHours() { return availableEndHours; }

  public ObjectProperty<Customer> selectedCustomerProperty() { return selectedCustomer; }
  public ObjectProperty<Bicycle> selectedBicycleProperty() { return selectedBicycle; }
  public ObjectProperty<DocumentType> selectedDocumentTypeProperty() { return selectedDocumentType; }

  public ObjectProperty<LocalDate> startDateProperty() { return startDate; }
  public ObjectProperty<LocalDate> endDateProperty() { return endDate; }

  public StringProperty startTimeProperty() { return startTime; }
  public StringProperty endTimeProperty() { return endTime; }
  public StringProperty documentNumberProperty() {
    return documentNumber;
  }
  public StringProperty depositAmountProperty() {
    return depositAmount;
  }
  public StringProperty customerErrorProperty() { return customerError; }
  public StringProperty bicycleErrorProperty() { return bicycleError; }
  public StringProperty startTimeErrorProperty() { return startTimeError; }
  public StringProperty endTimeErrorProperty() { return endTimeError; }

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