package org.example.bicyclesharing.services;

import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.User;
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

  public boolean existsByLoginExcept(String login, UUID currentUserId) {
    User user = userRepository.findByLogin(login);
    return user != null && !user.getId().equals(currentUserId);
  }

  @Override
  protected Repository<User, UUID> getRepository() {
    return userRepository;
  }


}
