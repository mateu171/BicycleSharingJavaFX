package org.example.bicyclesharing.domain.security;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

  private static final int WORK_FACTOR = 12;

  public static String hash(String plainPassword) {
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
  }

  public static boolean verify(String plainPassword, String hashedPassword) {
    if (plainPassword == null || hashedPassword == null) {
      return false;
    }
    return BCrypt.checkpw(plainPassword, hashedPassword);
  }
}
