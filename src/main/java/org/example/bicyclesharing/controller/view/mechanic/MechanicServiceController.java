package org.example.bicyclesharing.controller.view.mechanic;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.User;

public class MechanicServiceController extends BaseController {

  @FXML private Label titleLabel;

  @FXML
  public void initialize() {
    titleLabel.setText("Техобслуговування");
  }

  @Override
  public void setCurrentUser(User currentUser) {
  }
}