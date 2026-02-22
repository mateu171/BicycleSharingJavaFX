package org.example.bicyclesharing.services;


import org.example.bicyclesharing.util.CodeGenerate;

public class VerificationService {

  private final EmailService emailService;

  public VerificationService(EmailService emailService) {
    this.emailService = emailService;
  }

  public int sendVerificationCode(String email) {
    int code = CodeGenerate.generate();
    emailService.send(email, "Ваш код підтвердження", "Код: " + code);
    return code;
  }

}
