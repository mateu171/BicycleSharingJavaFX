package org.example.bicyclesharing.services;

@FunctionalInterface
public interface TransactionFunction<R> {
  R execute();
}
