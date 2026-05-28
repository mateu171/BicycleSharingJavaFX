package org.example.bicyclesharing.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.repository.Repository;
import org.example.bicyclesharing.repository.UserRepository;

public class UserService extends BaseService<User, UUID> {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    createDefaultAdminIfNotExists();
  }

  @Override
  protected Repository<User, UUID> getRepository() {
    return userRepository;
  }


  public List<User> findByFilters(String search, Role role) {
    return userRepository.findByFilters(search, role);
  }

  public void validateLoginIsUniqueForCreation(String login) {
    if (userRepository.existsByLoginActive(login)) {
      throw new BusinessException("error.user.login.exists");
    }
  }

  public void validateCanDelete(User userToDelete, User currentUser) {
    if (userToDelete == null) {
      throw new BusinessException("error.user.not_found");
    }

    if (currentUser != null && userToDelete.getId().equals(currentUser.getId())) {
      throw new BusinessException("error.user.delete.self");
    }

    if (userToDelete.getRole() == Role.ADMIN) {
      long activeAdminCount = countActiveAdmins();
      if (activeAdminCount <= 1) {
        throw new BusinessException("error.user.delete.last_admin");
      }
    }
  }

  public void validateRoleChange(User editingUser, Role newRole) {
    if (editingUser == null) {
      throw new BusinessException("error.user.not_found");
    }

    if (editingUser.getRole() == Role.ADMIN && newRole != Role.ADMIN) {
      long activeAdminCount = countActiveAdmins();
      if (activeAdminCount <= 1) {
        throw new BusinessException("error.user.edit.last_admin_role");
      }
    }
  }

  private long countActiveAdmins() {
    return userRepository.findByFilters(null, Role.ADMIN).size();
  }

  private void createDefaultAdminIfNotExists() {
    if (userRepository.existsByLoginActive("admin")) {
      return;
    }

    try {
      User admin = User.create("admin", "admin123", "admin@gmail.com", Role.ADMIN);
      userRepository.save(admin);
      System.out.println("Default admin created successfully.");
    } catch (Exception e) {
      System.err.println("Failed to create default admin: " + e.getMessage());
    }
  }

  @Override
  public boolean delete(User entity) {
    validateCanDelete(entity, null);

    entity.prepareForSoftDelete();

    userRepository.update(entity);

    return true;
  }
}