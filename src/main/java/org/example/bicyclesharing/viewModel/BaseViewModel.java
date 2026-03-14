package org.example.bicyclesharing.viewModel;

import org.example.bicyclesharing.domain.Impl.User;

public abstract class BaseViewModel {

  protected final User currentUser;

  protected BaseViewModel(User currentUser) {
    this.currentUser = currentUser;
  }

}
