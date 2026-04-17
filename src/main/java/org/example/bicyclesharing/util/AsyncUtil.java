package org.example.bicyclesharing.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.property.BooleanProperty;
import javafx.concurrent.Task;

public final class AsyncUtil {

  private AsyncUtil() {
  }

  public static <T> void runAsync(
      Supplier<T> backgroundAction,
      Consumer<T> onSuccess,
      Consumer<Throwable> onError,
      BooleanProperty loading
  ) {
    Task<T> task = new Task<>() {
      @Override
      protected T call() {
        return backgroundAction.get();
      }
    };

    if (loading != null) {
      loading.set(true);
    }

    task.setOnSucceeded(event -> {
      if (onSuccess != null) {
        onSuccess.accept(task.getValue());
      }
      if (loading != null) {
        loading.set(false);
      }
    });

    task.setOnFailed(event -> {
      if (onError != null) {
        onError.accept(task.getException());
      } else if (task.getException() != null) {
        task.getException().printStackTrace();
      }

      if (loading != null) {
        loading.set(false);
      }
    });

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }
}