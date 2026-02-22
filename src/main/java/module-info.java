module org.example.bicyclesharing {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;
  requires org.kordamp.bootstrapfx.core;
  requires jbcrypt;
  requires jakarta.mail;

  opens org.example.bicyclesharing to javafx.fxml;
  exports org.example.bicyclesharing;
}