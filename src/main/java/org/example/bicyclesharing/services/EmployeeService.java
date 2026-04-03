package org.example.bicyclesharing.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Employee;
import org.example.bicyclesharing.repository.EmployeeRepository;
import org.example.bicyclesharing.repository.Repository;

public class EmployeeService extends BaseService<Employee, UUID> {

  private final EmployeeRepository employeeRepository;

  public EmployeeService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  protected Repository<Employee, UUID> getRepository() {
    return employeeRepository;
  }

  public List<Employee> getByName(String name) {
    return employeeRepository.findByName(name);
  }

  public Optional<Employee> getById(UUID id) {
    return employeeRepository.findById(id);
  }
}
