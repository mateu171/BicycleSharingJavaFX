package org.example.bicyclesharing.services;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.example.bicyclesharing.exception.EmailExeption;

public class EmailService {

  private final String fromEmail;
  private final String password;

  public EmailService() {

    Properties config = new Properties();

    try (InputStream input = Files.newInputStream(
        Paths.get("config", "email.properties")
    )) {
      config.load(input);

      fromEmail = config.getProperty("email.username");
      password = config.getProperty("email.password");

    } catch (Exception e) {
      throw new RuntimeException("Помилка завантаження email конфігурації", e);
    }
  }

  public void send(String to, String subject, String text) {

    try {
      Properties properties = new Properties();
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.host", "smtp.gmail.com");
      properties.put("mail.smtp.port", "587");

      Session session = Session.getInstance(properties, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(fromEmail, password);
        }
      });

      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(fromEmail));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(subject, "UTF-8");
      message.setText(text, "UTF-8");

      Transport.send(message);

    } catch (Exception e) {
      throw new EmailExeption("Помилка надсилання email", e);
    }
  }
}