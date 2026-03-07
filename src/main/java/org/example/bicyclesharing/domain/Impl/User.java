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
  private double balance;

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
      Role role,
      double balance  // новий параметр
  ) {
    User user = new User();
    user.setId(id);
    user.login = login;
    user.hashedPassword = hashedPassword;
    user.email = email;
    user.role = role;
    user.balance = balance;  // встановлюємо баланс
    return user;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance)
  {
    this.balance = balance;
  }
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    cleanErrors("login");

    if (login == null || login.trim().isEmpty()) {
      addError("login", "error.login.empty");
    }
    else if (login.length() < 2 || login.length() > 50) {
      addError("login", "error.login.length");
    }

    this.login = login;
  }

  public String getHashedPassword() {
    return hashedPassword;
  }

  private void validatePassword(String password) {
    cleanErrors("password");
    if (password == null || password.trim().isEmpty()) {
      addError("password", "error.password.empty");
    }
    else if (password.length() < 8 || password.length() > 50) {
      addError("password", "error.password.length");
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
      addError("email", "error.email.empty");
    }
    else if (!email.matches("^[a-zA-Z0-9]+@gmail\\.com$")) {
      addError("email", "error.email.invalid");
    }
    this.email = email;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
