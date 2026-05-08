package org.example.bicyclesharing.viewModel.manager.modalViewModal;

import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditCustomerViewModel {

  private final CustomerService customerService;
  private final Customer editingCustomer;

  private final StringProperty titleText =
      new SimpleStringProperty();

  private final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");

  private final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  private final StringProperty fullNameLabelText =
      LocalizationManager.getStringProperty("manager.fullname");

  private final StringProperty phoneNumberLabelText =
      LocalizationManager.getStringProperty("manager.phonenumber");

  private final StringProperty documentNumberLabelText =
      LocalizationManager.getStringProperty("manager.documentnumber");

  private final StringProperty fullName =
      new SimpleStringProperty("");

  private final StringProperty phoneNumber =
      new SimpleStringProperty("");

  private final StringProperty documentNumber =
      new SimpleStringProperty("");

  private final StringProperty fullNameError =
      new SimpleStringProperty("");

  private final StringProperty phoneNumberError =
      new SimpleStringProperty("");

  private final StringProperty documentNumberError =
      new SimpleStringProperty("");

  public AddEditCustomerViewModel(
      CustomerService customerService,
      Customer editingCustomer
  ) {
    this.customerService = customerService;
    this.editingCustomer = editingCustomer;
  }

  public void initialize() {
    if (isEditMode()) {
      initializeEditMode();
    } else {
      initializeAddMode();
    }
  }

  private void initializeAddMode() {
    titleText.set(LocalizationManager.getStringByKey("manager.customer.add.title"));
  }

  private void initializeEditMode() {
    titleText.set(LocalizationManager.getStringByKey("manager.customer.edit.title"));

    fullName.set(editingCustomer.getFullName());
    phoneNumber.set(editingCustomer.getPhoneNumber());
    documentNumber.set(editingCustomer.getDocumentNumber());
  }

  public boolean save() {
    clearErrors();

    try {
      if (isEditMode()) {
        updateCustomer();
      } else {
        createCustomer();
      }

      return true;

    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;
    }
  }

  private void createCustomer() {
    Customer customer = new Customer(
        fullName.get(),
        phoneNumber.get(),
        documentNumber.get()
    );

    customerService.add(customer);
  }

  private void updateCustomer() {
    Customer validated = new Customer(
        fullName.get(),
        phoneNumber.get(),
        documentNumber.get()
    );

    editingCustomer.setFullName(validated.getFullName());
    editingCustomer.setPhoneNumber(validated.getPhoneNumber());
    editingCustomer.setDocumentNumber(validated.getDocumentNumber());

    customerService.update(editingCustomer);
  }

  private void applyValidationErrors(CustomEntityValidationExeption e) {
    e.getErrors().forEach((field, messages) -> {
      String text = messages.stream()
          .map(LocalizationManager::getStringByKey)
          .collect(Collectors.joining("\n"));

      switch (field) {
        case "fullName" -> fullNameError.set(text);
        case "phoneNumber" -> phoneNumberError.set(text);
        case "documentNumber" -> documentNumberError.set(text);
      }
    });
  }

  private void clearErrors() {
    fullNameError.set("");
    phoneNumberError.set("");
    documentNumberError.set("");
  }

  public boolean isEditMode() {
    return editingCustomer != null;
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

  public StringProperty fullNameLabelTextProperty() {
    return fullNameLabelText;
  }

  public StringProperty phoneNumberLabelTextProperty() {
    return phoneNumberLabelText;
  }

  public StringProperty documentNumberLabelTextProperty() {
    return documentNumberLabelText;
  }

  public StringProperty fullNameProperty() {
    return fullName;
  }

  public StringProperty phoneNumberProperty() {
    return phoneNumber;
  }

  public StringProperty documentNumberProperty() {
    return documentNumber;
  }

  public StringProperty fullNameErrorProperty() {
    return fullNameError;
  }

  public StringProperty phoneNumberErrorProperty() {
    return phoneNumberError;
  }

  public StringProperty documentNumberErrorProperty() {
    return documentNumberError;
  }
}