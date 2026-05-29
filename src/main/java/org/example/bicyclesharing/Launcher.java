package org.example.bicyclesharing;

import javafx.application.Application;
import org.example.bicyclesharing.util.AppConfig;

public class Launcher {

  public static void main(String[] args) {
    System.out.println(
        AppConfig.class.getClassLoader().getResource("config/email.properties")
    );
  Application.launch(HelloApplication.class, args);

  }
}
