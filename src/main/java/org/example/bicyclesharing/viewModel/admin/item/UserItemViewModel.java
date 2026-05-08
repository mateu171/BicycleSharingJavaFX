package org.example.bicyclesharing.viewModel.admin.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.LocalizationManager;

public class UserItemViewModel {

  private final User user;

  private final StringProperty loginText = new SimpleStringProperty();
  private final StringProperty emailText = new SimpleStringProperty();
  private final StringProperty roleTitleText = new SimpleStringProperty();
  private final StringProperty roleText = new SimpleStringProperty();
  private final StringProperty imagePath = new SimpleStringProperty();

  public UserItemViewModel(User user) {
    this.user = user;

    loginText.set(user.getLogin());
    emailText.set(user.getEmail());
    roleTitleText.set(LocalizationManager.getStringByKey("admin.users.role"));
    roleText.set(LocalizationManager.getStringByKey(user.getRole().getKey()));
    imagePath.set(user.getImagePath());
  }

  public User getUser() {
    return user;
  }

  public StringProperty loginTextProperty() {
    return loginText;
  }

  public StringProperty emailTextProperty() {
    return emailText;
  }

  public StringProperty roleTitleTextProperty() {
    return roleTitleText;
  }

  public StringProperty roleTextProperty() {
    return roleText;
  }

  public StringProperty imagePathProperty() {
    return imagePath;
  }
}