package org.example.bicyclesharing.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.domain.security.PasswordHasher;
import org.example.bicyclesharing.exception.BusinessException;
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

  public Optional<User> getById(UUID id) {
    return userRepository.findById(id);
  }

  @Override
  protected Repository<User, UUID> getRepository() {
    return userRepository;
  }

  public List<User> findByFilters(String search, Role role) {
    return userRepository.findByFilters(search, role);
  }

  public void validateCanDelete(User user, User currentUser) {
    if (user == null) {
      throw new BusinessException("error.user.not_found");
    }

    if (currentUser != null && user.getId().equals(currentUser.getId())) {
      throw new BusinessException("error.user.delete.self");
    }

    if (user.getRole() == Role.ADMIN) {
      long adminCount = getAll().stream()
          .filter(u -> u.getRole() == Role.ADMIN)
          .count();

      if (adminCount <= 1) {
        throw new BusinessException("error.user.delete.last_admin");
      }
    }
  }

  public void validateRoleChange(User editingUser, Role newRole) {
    if (editingUser == null) {
      throw new BusinessException("error.user.not_found");
    }

    if (editingUser.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
      long adminCount = getAll().stream()
          .filter(u -> u.getRole() == Role.ADMIN)
          .count();

      if (adminCount <= 1) {
        throw new BusinessException("error.user.edit.last_admin_role");
      }
    }
  }

}
