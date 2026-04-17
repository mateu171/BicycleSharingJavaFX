package org.example.bicyclesharing.viewModel.manager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;

public class ManagerCustomersViewModel extends AsyncViewModel {

  private final CustomerService customerService;
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

  public final StringProperty titleText = LocalizationManager.getStringProperty("manager.customers.title");
  public final StringProperty searchPromptText = LocalizationManager.getStringProperty("manager.customers.search");
  public final StringProperty editButtonText = LocalizationManager.getStringProperty("manager.customers.edit");
  public final StringProperty deleteButtonText = LocalizationManager.getStringProperty("manager.customers.delete");
  public final StringProperty addButtonText = LocalizationManager.getStringProperty("manager.customer.add.button");
  public final StringProperty countText = new SimpleStringProperty("");
  public final StringProperty searchText = new SimpleStringProperty("");


  public ManagerCustomersViewModel(User currentUser,CustomerService customerService) {
    super(currentUser);
    this.customerService = customerService;
  }

  public ObservableList<Customer> getCustomers() {
    return customers;
  }

  public void loadCustomersAsync()
  {
    runAsync(customerService::getAll,
        result -> {
      customers.setAll(result);
          updateCount();
        });
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("manager.customers.count") + ": " + customers.size()
    );
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    runAsync(
        () -> customerService.findByFilters(search),
        result -> {
          customers.setAll(result);
          updateCount();
        }
    );
  }

  public void deleteCustomer(Customer customer)
  {
    customerService.validateCanDelete(customer);
    customerService.deleteById(customer.getId());
    refreshAsync();
  }


  public void refreshAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();

    if (search.isBlank()) {
      loadCustomersAsync();
    } else {
      applyFiltersAsync();
    }
  }
}
