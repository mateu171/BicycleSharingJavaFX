package org.example.bicyclesharing.repository.db;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.repository.Repository;

public abstract class BaseRepositoryDB<T,ID> implements Repository<T, ID> {

  @Override
  public T save(T entity) {
    return null;
  }

  @Override
  public T update(T entity) {
    return null;
  }

  @Override
  public List<T> findAll() {
    return List.of();
  }

  @Override
  public Optional<T> findById(UUID uuid) {
    return Optional.empty();
  }

  @Override
  public boolean deleteById(UUID uuid) {
    return false;
  }

  @Override
  public boolean delete(T entity) {
    return false;
  }

  @Override
  public boolean existsById(UUID uuid) {
    return false;
  }

  @Override
  public long count() {
    return 0;
  }
}
