package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.Employee;

public interface EmployeeRepository extends Repository<Employee, UUID> {

  List<Employee> findByStationId(UUID id);

  List<Employee> findByName(String name);
}
