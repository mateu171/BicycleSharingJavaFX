module org.example.bicyclesharing {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;
  requires org.kordamp.bootstrapfx.core;
  requires jbcrypt;
  requires jakarta.mail;
  requires java.sql;
  requires java.desktop;
  requires javafx.web;
  requires java.prefs;
  requires jdk.jsobject;
  requires spring.jdbc;

  opens org.example.bicyclesharing to javafx.fxml;
  exports org.example.bicyclesharing;
  opens org.example.bicyclesharing.presentation to javafx.fxml;
  opens org.example.bicyclesharing.viewModel to javafx.fxml;
  opens org.example.bicyclesharing.controller to javafx.fxml;
}