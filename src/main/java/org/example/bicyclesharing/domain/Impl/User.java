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

  private User() {
    super();
  }

  public static User create(String login,
      String plainPassword,
      String email,
      Role role) {

    User user = new User();

    user.setLogin(login);
    user.validatePassword(plainPassword);
    user.setEmail(email);
    user.setRole(role);

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
      Role role
  ) {
    User user = new User();
    user.setId(id);
    user.login = login;
    user.hashedPassword = hashedPassword;
    user.email = email;
    user.role = role;
    return user;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    cleanErrors("login");
    if (login == null || login.trim().isEmpty()) {
      addError("login", "Логін неповинен бути пустим!");
    } else if (login.length() < 2 || login.length() > 50) {
      addError("login", "Логін повинен бути не менше 2 символів і небільше 50");
    }

    this.login = login;
  }

  public String getHashedPassword() {
    return hashedPassword;
  }

  private void validatePassword(String password) {
    cleanErrors("password");
    if (password == null || password.trim().isEmpty()) {
      addError("password", "Пароль неповинен бути пустим!");
    } else if (password.length() < 8 || password.length() > 50) {
      addError("password", "Пароль повинен бути не менше 8 символів і небільше 50");
    }
  }

  public void changePassword(String plainPassword) {

    validatePassword(plainPassword);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }

    this.hashedPassword = PasswordHasher.hash(plainPassword);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    cleanErrors("email");
    if (email == null || email.trim().isEmpty()) {
      addError("email", "Емайл не повинен бути пустим!");
    } else if (!email.matches("^[a-zA-Z0-9]+@gmail\\.com$")) {
      addError("email", "Невірний формат емайлу! Дозволено лише літери та цифри перед @gmail.com");
    }
    this.email = email;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    cleanErrors("role");
    if (role == null) {
      addError("role", "Роль не повинна бути пустим!");
    }

    this.role = role;
  }

  @Override
  public String toString() {
    return String.format("Логін: %s | Email: %s | Роль: %s", login, email, role);
  }
}
