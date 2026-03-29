package org.example.bicyclesharing.viewModel.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class UsersManagementViewModel extends BaseViewModel {

  private final UserService userService;
  private final ObservableList<User> users = FXCollections.observableArrayList();

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("admin.users.title");
  public final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("admin.users.search");
  public final StringProperty countText = new SimpleStringProperty("");

  public final StringProperty searchText = new SimpleStringProperty("");
  public final StringProperty selectedRoleFilter = new SimpleStringProperty(LocalizationManager.getStringByKey("all.text"));

  public UsersManagementViewModel(User currentUser, UserService userService) {
    super(currentUser);
    this.userService = userService;
    loadUsers();
  }

  public ObservableList<User> getUsers() {
    return users;
  }

  public void loadUsers() {
    users.setAll(userService.getAll());
    updateCount();
  }

  public void applyFilters() {
    List<User> allUsers = userService.getAll();

    String search = searchText.get() == null ? "" : searchText.get().trim().toLowerCase(Locale.ROOT);
    String roleFilter = selectedRoleFilter.get() == null
        ? LocalizationManager.getStringByKey("all.text")
        : selectedRoleFilter.get();

    List<User> filtered = allUsers.stream()
        .filter(user -> {
          boolean matchesSearch =
              search.isEmpty()
                  || user.getLogin().toLowerCase(Locale.ROOT).contains(search)
                  || user.getEmail().toLowerCase(Locale.ROOT).contains(search);

          boolean matchesRole =
              roleFilter.equals(LocalizationManager.getStringByKey("all.text"))
                  || LocalizationManager.getStringByKey(user.getRole().getKey()).equals(roleFilter);

          return matchesSearch && matchesRole;
        })
        .collect(Collectors.toList());

    users.setAll(filtered);
    updateCount();
  }

  public void deleteUser(User user) {
    if (user == null) return;
    userService.deleteById(user.getId());
    applyFilters();
  }

  private void updateCount() {
    countText.set(
        LocalizationManager.getStringByKey("admin.users.count") + ": " + users.size()
    );
  }

  public void changeRole(User user, Role newRole) {
    if (user == null || newRole == null) return;

    user.setRole(newRole);
    userService.update(user);
    applyFilters();
  }
}