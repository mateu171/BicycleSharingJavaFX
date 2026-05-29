package org.example.bicyclesharing.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.example.bicyclesharing.exception.EmailExeption;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class EmailService {

  private final String fromEmail;
  private final String password;

  public EmailService() {

    Properties config = new Properties();
    InputStream input = null;

    try {
      if (Files.exists(Paths.get("config/email.properties"))) {
        input = Files.newInputStream(Paths.get("config/email.properties"));
      }

      if (input == null) {
        input = getClass().getClassLoader()
            .getResourceAsStream("config/email.properties");
      }

      if (input == null) {
        throw new RuntimeException(
            "Не знайдено config/email.properties ні в файловій системі, ні в resources"
        );
      }

      config.load(input);

      fromEmail = config.getProperty("email.username");
      password = config.getProperty("email.password");

    } catch (Exception e) {
      throw new RuntimeException("Помилка завантаження email конфігурації", e);
    } finally {
      try {
        if (input != null) input.close();
      } catch (Exception ignored) {}
    }
  }

  public void send(String to, String subject, String text) {

    try {
      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", "smtp.gmail.com");
      props.put("mail.smtp.port", "587");

      Session session = Session.getInstance(props, new Authenticator() {
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