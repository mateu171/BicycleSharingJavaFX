package org.example.bicyclesharing.services;

import java.util.List;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.domain.security.PasswordHasher;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.UserRepository;

public class UserService extends BaseService<User, UUID> {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;

  public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
  }

  public boolean existsByLogin(String login) {
    return userRepository.findByLogin(login) != null;
  }

  @Override
  protected Repository<User, UUID> getRepository() {
    return userRepository;
  }

  @Override
  public User add(User entity) {
    entity.setPassword(PasswordHasher.hash(entity.getPassword()));
    return getRepository().save(entity);
  }

  public User getByLogin(String login) {
    return userRepository.findByLogin(login);
  }

  public List<User> getByRole(Role role) {
    return userRepository.findByRole(role);
  }

}
