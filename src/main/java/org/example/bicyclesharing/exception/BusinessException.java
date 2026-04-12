package org.example.bicyclesharing.exception;

public class BusinessException extends RuntimeException {

  public BusinessException(String messageKey) {
    super(messageKey);
  }
}
