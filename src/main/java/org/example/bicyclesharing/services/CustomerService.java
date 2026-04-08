package org.example.bicyclesharing.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Bicycle;
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


  public Optional<Customer> getById(UUID id) {
    return repository.findById(id);
  }

  public List<Customer> findByFilters(String search) {
    return repository.findByFilters(search);
  }
}
