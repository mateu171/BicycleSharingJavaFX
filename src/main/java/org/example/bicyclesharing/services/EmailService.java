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
          return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
        }
      });

      MimeMessage message = new MimeMessage(session);

      message.setFrom(new InternetAddress(FROM_EMAIL));

      message.setRecipients(
          Message.RecipientType.TO,
          InternetAddress.parse(to)
      );

      message.setSubject(subject, "UTF-8");
      message.setText(text, "UTF-8");

      Transport.send(message);

    } catch (Exception e) {
      throw new EmailExeption("Помилка надсилання email", e);
    }
  }
}