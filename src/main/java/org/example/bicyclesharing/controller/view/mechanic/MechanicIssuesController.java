package org.example.bicyclesharing.controller.view.mechanic;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.User;

public class MechanicIssuesController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private TableView<Object> issuesTable;
  @FXML private TableColumn<Object, String> bikeColumn;
  @FXML private TableColumn<Object, String> problemColumn;
  @FXML private TableColumn<Object, String> dateColumn;
  @FXML private TableColumn<Object, String> statusColumn;

  @FXML
  public void initialize() {
    titleLabel.setText("Проблемні велосипеди");
  }

  @Override
  public void setCurrentUser(User currentUser) {
  }
}