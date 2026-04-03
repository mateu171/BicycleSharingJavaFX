package org.example.bicyclesharing.services;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Customer;
import org.example.bicyclesharing.repository.CustomerRepository;
import org.example.bicyclesharing.repository.Repository;

public class CustomerService extends BaseService<Customer, UUID>{

  private final CustomerRepository repository;

  public CustomerService(CustomerRepository repository) {
    this.repository = repository;
  }

  @Override
  protected Repository<Customer, UUID> getRepository() {
    return repository;
  }

  public List<Customer> getByName(String name)
  {
    return repository.findByName(name);
  }
}
