package org.example.bicyclesharing.util;

@FunctionalInterface
public interface ControllerInitializer<T> {
  void init(T controller);
}
