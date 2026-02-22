package org.example.bicyclesharing.repository;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;

public interface UserRepository extends Repository<User, UUID> {

  List<User> findByRole(Role role);

  User findByLogin(String login);
}
