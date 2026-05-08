package org.example.bicyclesharing.viewModel.manager.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.Customer;

public class CustomerItemViewModel {

  private final Customer customer;

  private final StringProperty fullNameText = new SimpleStringProperty();
  private final StringProperty phoneNumberText = new SimpleStringProperty();
  private final StringProperty documentNumberText = new SimpleStringProperty();

  public CustomerItemViewModel(Customer customer) {
    this.customer = customer;

    fullNameText.set(customer.getFullName());
    phoneNumberText.set(customer.getPhoneNumber());
    documentNumberText.set(customer.getDocumentNumber());
  }

  public Customer getCustomer() {
    return customer;
  }

  public StringProperty fullNameTextProperty() {
    return fullNameText;
  }

  public StringProperty phoneNumberTextProperty() {
    return phoneNumberText;
  }

  public StringProperty documentNumberTextProperty() {
    return documentNumberText;
  }
}