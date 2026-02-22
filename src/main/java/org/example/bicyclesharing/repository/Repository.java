package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {

  T save(T entity);

  T update(T entity);

  List<T> findAll();

  Optional<T> findById(ID id);

  boolean deleteById(ID id);

  boolean delete(T entity);

  boolean existsById(ID id);

  long count();


}
