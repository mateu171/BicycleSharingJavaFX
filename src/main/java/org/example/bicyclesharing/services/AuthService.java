package org.example.bicyclesharing.services;


import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.security.PasswordHasher;
import org.example.bicyclesharing.exception.AuthException;
import org.example.bicyclesharing.repository.UserRepository;

public class AuthService {

  private final UserRepository userRepository;

  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User authenticate(String login, String password) {
    User user = userRepository.findByLogin(login);

    if (user == null || !PasswordHasher.verify(password, user.getHashedPassword())) {
      throw new AuthException("Невірний логін або пароль");
    }

    return user;
  }
}
