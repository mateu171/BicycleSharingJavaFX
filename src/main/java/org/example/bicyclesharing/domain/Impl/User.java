package org.example.bicyclesharing.domain.Impl;

import java.util.UUID;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.domain.security.PasswordHasher;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class User extends BaseEntity {

  private String login;
  private String hashedPassword;
  private String email;
  private Role role;
  private String imagePath;

  private User() {
    super();
  }

  public static User create(
      String login,
      String plainPassword,
      String email,
      Role role
  ) {
    User user = new User();

    user.setLogin(login);
    user.validatePassword(plainPassword);
    user.setEmail(email);
    user.setRole(role);
    user.setImagePath(null);

    if (!user.isValid()) {
      throw new CustomEntityValidationExeption(user.getErrors());
    }

    user.hashedPassword = PasswordHasher.hash(plainPassword);
    return user;
  }

  public static User fromDatabase(
      UUID id,
      String login,
      String hashedPassword,
      String email,
      Role role,
      String imagePath
  ) {
    User user = new User();
    user.setId(id);
    user.login = login;
    user.hashedPassword = hashedPassword;
    user.email = email;
    user.role = role;
    user.imagePath = imagePath;
    return user;
  }

  public void updateProfile(String login, String email, Role role, String imagePath) {
    setLogin(login);
    setEmail(email);
    setRole(role);
    setImagePath(imagePath);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    cleanErrors("login");

    String value = login == null ? null : login.trim();

    if (value == null || value.isEmpty()) {
      addError("login", "error.login.empty");
    } else if (value.length() < 2 || value.length() > 50) {
      addError("login", "error.login.length");
    }

    this.login = value;
  }

  public String getHashedPassword() {
    return hashedPassword;
  }

  private void validatePassword(String password) {
    cleanErrors("password");

    String value = password == null ? null : password.trim();

    if (value == null || value.isEmpty()) {
      addError("password", "error.password.empty");
    } else if (value.length() < 8 || value.length() > 50) {
      addError("password", "error.password.length");
    }
  }

  public void changePassword(String plainPassword) {
    validatePassword(plainPassword);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }

    this.hashedPassword = PasswordHasher.hash(plainPassword.trim());
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    cleanErrors("email");

    String value = email == null ? null : email.trim();

    if (value == null || value.isEmpty()) {
      addError("email", "error.email.empty");
    } else if (!value.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
      addError("email", "error.email.invalid");
    }

    this.email = value;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    cleanErrors("role");

    if (role == null) {
      addError("role", "admin.users.role.empty");
    }

    this.role = role;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    cleanErrors("imagePath");

    String value = imagePath == null ? null : imagePath.trim();

    if (value != null && value.length() > 255) {
      addError("imagePath", "error.image.path.length");
    }

    this.imagePath = (value == null || value.isEmpty()) ? null : value;
  }
}