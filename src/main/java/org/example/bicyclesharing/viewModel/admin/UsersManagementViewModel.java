package org.example.bicyclesharing.viewModel.admin;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;
import org.example.bicyclesharing.viewModel.admin.item.UserItemViewModel;

public class UsersManagementViewModel extends AsyncViewModel {

  private final UserService userService;

  private final ObservableList<UserItemViewModel> users =
      FXCollections.observableArrayList();

  private final ObservableList<String> roleFilters =
      FXCollections.observableArrayList();

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("admin.users.title");

  private final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("admin.users.search");

  private final StringProperty addButtonText =
      LocalizationManager.getStringProperty("admin.users.add");

  private final StringProperty countText =
      new SimpleStringProperty("");

  private final StringProperty searchText =
      new SimpleStringProperty("");

  private final StringProperty selectedRoleFilter =
      new SimpleStringProperty();

  private final BooleanProperty loading =
      new SimpleBooleanProperty(false);

  public UsersManagementViewModel(
      User currentUser,
      UserService userService
  ) {
    super(currentUser);
    this.userService = userService;

    initializeRoleFilters();
  }

  public void initialize() {
    loadUsersAsync();
  }

  private void initializeRoleFilters() {
    roleFilters.setAll(
        LocalizationManager.getStringByKey("all.text"),
        LocalizationManager.getStringByKey(Role.ADMIN.getKey()),
        LocalizationManager.getStringByKey(Role.MANAGER.getKey()),
        LocalizationManager.getStringByKey(Role.MECHANIC.getKey())
    );

    selectedRoleFilter.set(LocalizationManager.getStringByKey("all.text"));
  }

  public void loadUsersAsync() {
    loading.set(true);

    runAsync(
        userService::getAll,
        result -> {
          setUsers(result);
          loading.set(false);
        }
    );
  }

  public void applyFiltersAsync() {
    loading.set(true);

    String search = searchText.get() == null ? "" : searchText.get().trim();
    Role roleFilter = resolveSelectedRole();

    runAsync(
        () -> userService.findByFilters(search, roleFilter),
        result -> {
          setUsers(result);
          loading.set(false);
        }
    );
  }

  public void refreshAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();

    boolean noFilters = search.isBlank()
        && isAllFilter(selectedRoleFilter.get());

    if (noFilters) {
      loadUsersAsync();
    } else {
      applyFiltersAsync();
    }
  }

  public void deleteUser(UserItemViewModel item) {
    if (item == null) {
      return;
    }

    User user = item.getUser();

    userService.validateCanDelete(user, currentUser);
    userService.delete(user);
    refreshAsync();
  }

  private void setUsers(List<User> result) {
    users.setAll(
        result.stream()
            .map(UserItemViewModel::new)
            .toList()
    );

    updateCount();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("admin.users.count")
            + ": "
            + users.size()
    );
  }

  private Role resolveSelectedRole() {
    String roleFilterText = selectedRoleFilter.get();

    if (isAllFilter(roleFilterText)) {
      return null;
    }

    for (Role role : Role.values()) {
      String localizedRole = LocalizationManager.getStringByKey(role.getKey());

      if (localizedRole.equals(roleFilterText)) {
        return role;
      }
    }

    return null;
  }

  private boolean isAllFilter(String value) {
    return value == null
        || value.equals(LocalizationManager.getStringByKey("all.text"));
  }

  public ObservableList<UserItemViewModel> getUsers() {
    return users;
  }

  public ObservableList<String> getRoleFilters() {
    return roleFilters;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
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

  public StringProperty selectedRoleFilterProperty() {
    return selectedRoleFilter;
  }

  public BooleanProperty loadingProperty() {
    return loading;
  }
}