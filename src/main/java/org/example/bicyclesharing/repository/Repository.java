package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T, ID> {

  T save(T entity);

  T update(T entity);

  List<T> findAll();

  Optional<T> findById(ID id);

  boolean deleteById(ID id);

  Optional<T> findById(UUID uuid);

  boolean deleteById(UUID uuid);

  boolean delete(T entity);

  boolean existsById(ID id);

  boolean existsById(UUID uuid);

  long count();


}
