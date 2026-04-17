package org.example.bicyclesharing.viewModel;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AsyncUtil;

public abstract class AsyncViewModel extends BaseViewModel {

  public final BooleanProperty loading = new SimpleBooleanProperty(false);

  protected AsyncViewModel(User currentUser) {
    super(currentUser);
  }

  protected <T> void runAsync(
      Supplier<T> backgroundAction,
      Consumer<T> onSuccess
  ) {
    AsyncUtil.runAsync(
        backgroundAction,
        onSuccess,
        Throwable::printStackTrace,
        loading
    );
  }

  protected <T> void runAsync(
      Supplier<T> backgroundAction,
      Consumer<T> onSuccess,
      Consumer<Throwable> onError
  ) {
    AsyncUtil.runAsync(
        backgroundAction,
        onSuccess,
        onError,
        loading
    );
  }
}