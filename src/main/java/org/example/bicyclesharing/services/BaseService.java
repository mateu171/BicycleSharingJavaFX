package org.example.bicyclesharing.services;

import java.util.List;
import org.example.bicyclesharing.repository.Repository;

public abstract class BaseService<T, ID> {

  protected abstract Repository<T, ID> getRepository();

  public T add(T entity) {
    return getRepository().save(entity);
  }

  public List<T> getAll() {
    return getRepository().findAll();
  }

  public boolean delete(T entity) {
    return getRepository().delete(entity);
  }

  public boolean deleteById(ID id) {
    return getRepository().deleteById(id);
  }

  public T update(T entity) {
    return getRepository().update(entity);
  }

  public boolean existsById(ID id) {
    return getRepository().existsById(id);
  }

  public long count() {
    return getRepository().count();
  }
}
