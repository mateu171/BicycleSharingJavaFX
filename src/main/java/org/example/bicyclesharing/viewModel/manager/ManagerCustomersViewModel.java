package org.example.bicyclesharing.viewModel.manager;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.CustomerService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class ManagerCustomersViewModel extends BaseViewModel {

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
    loadCustomers();
  }

  public ObservableList<Customer> getCustomers() {
    return customers;
  }

  public void loadCustomers()
  {
    customers.setAll(customerService.getAll());
    updateCount();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("manager.customers.count") + ": " + customers.size()
    );
  }

  public void applyFilters() {
    List<Customer> allCustomers = customerService.getAll();

    String search = searchText.get() == null ? "" : searchText.get().trim().toLowerCase(Locale.ROOT);

    List<Customer> filtered = allCustomers.stream()
        .filter(user -> {
          boolean matchesSearch =
              search.isEmpty()
                  || user.getFullName().toLowerCase(Locale.ROOT).contains(search);

          return matchesSearch;
        })
        .collect(Collectors.toList());

    customers.setAll(filtered);
    updateCount();
  }

  public void deleteCustomer(Customer customer)
  {
    customerService.deleteById(customer.getId());
    applyFilters();
  }

}
