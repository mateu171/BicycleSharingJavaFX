package org.example.bicyclesharing.services;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.example.bicyclesharing.exception.EmailExeption;

public class EmailService {

  private static final String FROM_EMAIL = "morgus288@gmail.com";
  private static final String PASSWORD = "zbnb qxcn qpmm deks";

  public EmailService() {
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
          return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
        }
      });

      MimeMessage message = new MimeMessage(session);
      message.setFrom(new InternetAddress(FROM_EMAIL));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject(subject, "UTF-8");
      message.setText(text, "UTF-8");

      Transport.send(message);
    } catch (Exception e) {
      throw new EmailExeption("Помилка надсилання email", e);
    }
  }
}
