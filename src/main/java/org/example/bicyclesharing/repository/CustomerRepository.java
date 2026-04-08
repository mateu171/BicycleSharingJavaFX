package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Customer;

public interface CustomerRepository extends Repository<Customer, UUID> {

  List<Customer> findByFilters(String search);
}
