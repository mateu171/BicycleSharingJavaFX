package org.example.bicyclesharing.domain.Impl;

import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class User extends BaseEntity {

  private String login;
  private String password;
  private String email;
  private Role role;

  private User() {
    super();
  }

  public User(String login, String password, String email, Role role) {
    this();
    setLogin(login);
    setPassword(password);
    setEmail(email);
    setRole(role);

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    cleanErrors("login");
    if (login == null || login.trim().isEmpty()) {
      addError("login", "Логін неповинен бути пустим!");
    } else if (login.length() < 2 || login.length() > 50) {
      addError("login", "Логін повинен бути не менше 2 символі і небільше 50");
    }

    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    cleanErrors("password");
    if (password == null || password.trim().isEmpty()) {
      addError("password", "Пароль неповинен бути пустим!");
    } else if (password.length() < 8 || password.length() > 50) {
      addError("password", "password повинен бути не менше 8 символі і небільше 50");
    }

    this.password = password;
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
