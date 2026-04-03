package org.example.bicyclesharing.domain.Impl;

import java.util.UUID;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class Customer extends BaseEntity {

  private String fullName;
  private String phoneNumber;
  private String documentNumber;
  private UUID activeRent;

  private Customer() {
    super();
  }

  public Customer(String fullName, String phoneNumber, String documentNumber) {
    this();
    setFullName(fullName);
    setPhoneNumber(phoneNumber);
    setDocumentNumber(documentNumber);
    this.activeRent = null;

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public static Customer fromDatabase(UUID id,
      String fullName,
      String phoneNumber,
      String documentNumber,
      UUID activeRent) {

    Customer customer = new Customer();
    customer.setId(id);
    customer.fullName = fullName;
    customer.phoneNumber = phoneNumber;
    customer.documentNumber = documentNumber;
    customer.activeRent = activeRent;
    return customer;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    cleanErrors("fullName");

    if (fullName == null || fullName.trim().isEmpty()) {
      addError("fullName", "customer.fullName.empty");
    }
    else if (!fullName.matches("^[А-ЯІЇЄ][а-яіїє'\\-]+ [А-ЯІЇЄ][а-яіїє'\\-]+ [А-ЯІЇЄ][а-яіїє'\\-]+$")) {
      addError("fullName", "customer.fullName.invalid");
    }

    this.fullName = fullName;
  }


  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    cleanErrors("phoneNumber");

    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      addError("phoneNumber", "customer.phone.empty");
    }
    else if (!phoneNumber.matches("^\\+?[0-9]{10,15}$")) {
      addError("phoneNumber", "customer.phone.invalid");
    }

    this.phoneNumber = phoneNumber;
  }


  public String getDocumentNumber() {
    return documentNumber;
  }

  public void setDocumentNumber(String documentNumber) {
    cleanErrors("documentNumber");

    if (documentNumber == null || documentNumber.trim().isEmpty()) {
      addError("documentNumber", "customer.document.empty");
    }
    else if (!documentNumber.matches("^[A-ZА-ЯІЇЄ0-9]{5,20}$")) {
      addError("documentNumber", "customer.document.invalid");
    }

    this.documentNumber = documentNumber;
  }

  public UUID getActiveRent() {
    return activeRent;
  }

  public void setActiveRent(UUID activeRent) {
    this.activeRent = activeRent;
  }
}