package org.example.bicyclesharing.viewModel.admin;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.util.AsyncUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class UsersManagementViewModel extends AsyncViewModel {

  private final UserService userService;
  private final ObservableList<User> users = FXCollections.observableArrayList();

  public final StringProperty titleText = LocalizationManager.getStringProperty("admin.users.title");
  public final StringProperty searchPromptText = LocalizationManager.getStringProperty("admin.users.search");
  public final StringProperty addButtonText = LocalizationManager.getStringProperty("admin.users.add");
  public final StringProperty countText = new SimpleStringProperty("");

  public final StringProperty searchText = new SimpleStringProperty("");
  public final StringProperty selectedRoleFilter = new SimpleStringProperty(LocalizationManager.getStringByKey("all.text"));

  public final BooleanProperty loading = new SimpleBooleanProperty(false);

  public UsersManagementViewModel(User currentUser, UserService userService) {
    super(currentUser);
    this.userService = userService;
    loadUsersAsync();
  }

  public ObservableList<User> getUsers() {
    return users;
  }

  public void loadUsersAsync() {
    runAsync(
        userService::getAll,
        result -> {
          users.setAll(result);
          updateCount();
        }
    );
  }

  public void applyFiltersAsync() {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    String roleFilterText = selectedRoleFilter.get();
    Role roleFilter = null;

    if (roleFilterText != null &&
        !roleFilterText.equals(LocalizationManager.getStringByKey("all.text"))) {

      for (Role role : Role.values()) {
        String localizedRole = LocalizationManager.getStringByKey(role.getKey());
        if (localizedRole.equals(roleFilterText)) {
          roleFilter = role;
          break;
        }
      }
    }

    Role finalRoleFilter = roleFilter;
    String finalSearch = search;

    runAsync(
        () -> userService.findByFilters(finalSearch,finalRoleFilter),
        result -> {
          users.setAll(result);
          updateCount();
        }
    );
  }

  public void refreshAsync()
  {
    String search = searchText.get() == null ? "" : searchText.get().trim();
    String roleFilterText = selectedRoleFilter.get();

    boolean noFilters = search.isBlank()
        && (roleFilterText == null
        || roleFilterText.equals(LocalizationManager.getStringByKey("all.text")));

    if (noFilters) {
      loadUsersAsync();
    } else {
      applyFiltersAsync();
    }
  }

  public void deleteUser(User user) {
    if (user == null) return;

    userService.validateCanDelete(user,currentUser);
    userService.deleteById(user.getId());
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("admin.users.count") + ": " + users.size()
    );
  }
}