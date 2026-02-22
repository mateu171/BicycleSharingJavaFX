package org.example.bicyclesharing.exception;

public class EmailExeption extends RuntimeException {

  public EmailExeption(String message) {
    super(message);
  }

  public EmailExeption(String message, Throwable cause) {
    super(message, cause);
  }
}
