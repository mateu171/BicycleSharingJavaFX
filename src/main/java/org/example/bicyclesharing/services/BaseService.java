package org.example.bicyclesharing.services;

import java.util.List;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.db.BaseRepositoryDB;

public abstract class BaseService<T, ID> {

  protected abstract Repository<T, ID> getRepository();

  protected void executeInTransaction(Runnable action) {
    Repository<T, ID> repo = getRepository();

    if (repo instanceof BaseRepositoryDB) {
      BaseRepositoryDB<T, ID> dbRepo = (BaseRepositoryDB<T, ID>) repo;
      dbRepo.executeInTransaction(conn -> {   // явно через TransactionCallback
        action.run();
      });
    } else {
      action.run();
    }
  }

  protected <R> R executeInTransactionWithResult(TransactionFunction<R> function) {
    Repository<T, ID> repo = getRepository();

    if (repo instanceof BaseRepositoryDB) {
      BaseRepositoryDB<T, ID> dbRepo = (BaseRepositoryDB<T, ID>) repo;
      final Object[] result = new Object[1];

      dbRepo.executeInTransaction(conn -> {
        result[0] = function.execute();
      });

      return (R) result[0];
    } else {
      return function.execute();
    }
  }

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