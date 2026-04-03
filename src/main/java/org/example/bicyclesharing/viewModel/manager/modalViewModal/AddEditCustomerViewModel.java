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

  public final StringProperty titleText = new SimpleStringProperty();
  public final StringProperty saveButtonText =
      LocalizationManager.getStringProperty("save.button");
  public final StringProperty cancelButtonText =
      LocalizationManager.getStringProperty("cancel.button");

  public final StringProperty fullNameLabelText =
      LocalizationManager.getStringProperty("manager.fullname");
  public final StringProperty phoneNumberLabelText =
      LocalizationManager.getStringProperty("manager.phonenumber");
  public final StringProperty documentNumberLabelText =
      LocalizationManager.getStringProperty("manager.documentnumber");

  public final StringProperty fullName = new SimpleStringProperty("");
  public final StringProperty phoneNumber = new SimpleStringProperty("");
  public final StringProperty documentNumber = new SimpleStringProperty("");

  public final StringProperty fullNameError = new SimpleStringProperty("");
  public final StringProperty phoneNumberError = new SimpleStringProperty("");
  public final StringProperty documentNumberError = new SimpleStringProperty("");

  public AddEditCustomerViewModel(CustomerService customerService,Customer editingCustomer) {
    this.customerService = customerService;
    this.editingCustomer = editingCustomer;

    if(editingCustomer == null)
    {
      titleText.set(LocalizationManager.getStringByKey("manager.customer.add.title"));
    }
    else
    {
      titleText.set(LocalizationManager.getStringByKey("manager.customer.edit.title"));

    }
  }

  public boolean save()
  {
    clearErrors();

    try {
      if (editingCustomer == null) {
      Customer customer = new Customer(fullName.get(), phoneNumber.get(), documentNumber.get());
      customerService.add(customer);
    } else
      {
        String fullNameValue = isBlank(fullName.get())
            ? editingCustomer.getFullName()
            : fullName.get().trim();

        String phoneNumberValue = isBlank(phoneNumber.get())
            ? editingCustomer.getPhoneNumber()
            : phoneNumber.get().trim();

        String documentNumberValue = isBlank(documentNumber.get())
            ? editingCustomer.getDocumentNumber()
            : documentNumber.get().trim();

        Customer validated = new Customer(
            fullNameValue,
            phoneNumberValue,
            documentNumberValue
        );

        editingCustomer.setFullName(validated.getFullName());
        editingCustomer.setPhoneNumber(validated.getPhoneNumber());
        editingCustomer.setDocumentNumber(validated.getDocumentNumber());
        editingCustomer.setActiveRent(validated.getActiveRent());

        if(!editingCustomer.isValid())
        {
          throw new CustomEntityValidationExeption(editingCustomer.getErrors());
        }
        customerService.update(editingCustomer);
      }
      return true;
    }
    catch (CustomEntityValidationExeption e) {
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
      return false;
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
  private void clearErrors()
  {
    fullNameError.set("");
    phoneNumberError.set("");
    documentNumberError.set("");
  }

  public boolean isEditMode() {
    return editingCustomer != null;
  }
}
