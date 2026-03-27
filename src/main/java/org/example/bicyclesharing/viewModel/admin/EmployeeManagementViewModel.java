package org.example.bicyclesharing.viewModel.admin;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Employee;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.services.EmployeeService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class EmployeeManagementViewModel extends BaseViewModel {

  private final EmployeeService employeeService;
  private final ObservableList<Employee> employees = FXCollections.observableArrayList();

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("admin.employees.title");
  public final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("admin.employees.search");
  public final StringProperty addEmployeeButtonText =
      LocalizationManager.getStringProperty("admin.employees.add");
  public final StringProperty countText = new SimpleStringProperty("");

  public final StringProperty searchText = new SimpleStringProperty("");
  public final StringProperty selectedTypeFilter = new SimpleStringProperty("ALL");

  public EmployeeManagementViewModel(User currentUser, EmployeeService employeeService) {
    super(currentUser);
    this.employeeService = employeeService;
    load();
  }

  public ObservableList<Employee> getEmployees() {
    return employees;
  }

  public void load() {
    employees.setAll(employeeService.getAll());
    updateCount();
  }

  public void applyFilters() {
    String search = searchText.get() == null ? "" : searchText.get().trim().toLowerCase(Locale.ROOT);
    String typeFilter = selectedTypeFilter.get() == null ? "ALL" : selectedTypeFilter.get();

    List<Employee> filtered = employeeService.getAll().stream()
        .filter(employee -> {
          boolean matchesSearch =
              search.isEmpty()
                  || employee.getName().toLowerCase(Locale.ROOT).contains(search)
                  || employee.getPhoneNumber().toLowerCase(Locale.ROOT).contains(search);

          boolean matchesType =
              typeFilter.equals("ALL")
                  || employee.getType().name().equals(typeFilter);

          return matchesSearch && matchesType;
        })
        .collect(Collectors.toList());

    employees.setAll(filtered);
    updateCount();
  }

  public void delete(Employee employee) {
    if (employee == null) return;
    employeeService.deleteById(employee.getId());
    applyFilters();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("admin.employees.count") + ": " + employees.size()
    );
  }
}