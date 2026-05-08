package org.example.bicyclesharing.viewModel.manager;

import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;
import org.example.bicyclesharing.viewModel.manager.item.CustomerItemViewModel;

public class ManagerCustomersViewModel extends AsyncViewModel {

  private final CustomerService customerService;

  private final ObservableList<CustomerItemViewModel> customers =
      FXCollections.observableArrayList();

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("manager.customers.title");

  private final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("manager.customers.search");

  private final StringProperty editButtonText =
      LocalizationManager.getStringProperty("manager.customers.edit");

  private final StringProperty deleteButtonText =
      LocalizationManager.getStringProperty("manager.customers.delete");

  private final StringProperty addButtonText =
      LocalizationManager.getStringProperty("manager.customer.add.button");

  private final StringProperty countText =
      new SimpleStringProperty("");

  private final StringProperty searchText =
      new SimpleStringProperty("");

  public ManagerCustomersViewModel(
      User currentUser,
      CustomerService customerService
  ) {
    super(currentUser);
    this.customerService = customerService;
  }

  public void initialize() {
    loadCustomersAsync();
  }

  public void loadCustomersAsync() {
    runAsync(
        customerService::getAll,
        this::setCustomers
    );
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();

    runAsync(
        () -> customerService.findByFilters(search),
        this::setCustomers
    );
  }

  public void refreshAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();

    if (search.isBlank()) {
      loadCustomersAsync();
    } else {
      applyFiltersAsync();
    }
  }

  public void deleteCustomer(CustomerItemViewModel item) {
    if (item == null) {
      return;
    }

    Customer customer = item.getCustomer();

    customerService.validateCanDelete(customer);
    customerService.deleteById(customer.getId());

    refreshAsync();
  }

  private void setCustomers(List<Customer> result) {
    customers.setAll(
        result.stream()
            .map(CustomerItemViewModel::new)
            .toList()
    );

    updateCount();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("manager.customers.count")
            + ": "
            + customers.size()
    );
  }

  public ObservableList<CustomerItemViewModel> getCustomers() {
    return customers;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
  }

  public StringProperty editButtonTextProperty() {
    return editButtonText;
  }

  public StringProperty deleteButtonTextProperty() {
    return deleteButtonText;
  }

  public StringProperty addButtonTextProperty() {
    return addButtonText;
  }

  public StringProperty countTextProperty() {
    return countText;
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }
}