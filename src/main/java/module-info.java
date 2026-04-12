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
  opens org.example.bicyclesharing.presentation.view to javafx.fxml;
  opens org.example.bicyclesharing.presentation.window to javafx.fxml;
  opens org.example.bicyclesharing.viewModel to javafx.fxml;
  opens org.example.bicyclesharing.controller.window to javafx.fxml;
  opens org.example.bicyclesharing.controller.view to javafx.fxml;
  opens org.example.bicyclesharing.controller.view.admin to javafx.fxml;
  opens org.example.bicyclesharing.controller.view.admin.modalController to javafx.fxml,javafx.web;
  opens org.example.bicyclesharing.controller.view.mechanic to javafx.fxml;
  opens org.example.bicyclesharing.controller.view.manager to javafx.fxml;
  opens org.example.bicyclesharing.controller.view.mechanic.modalController to javafx.fxml;
  opens org.example.bicyclesharing.controller.view.shared to javafx.fxml;
  opens org.example.bicyclesharing.controller.view.manager.modalController to javafx.fxml;
  opens org.example.bicyclesharing.domain.Impl to javafx.base;
}