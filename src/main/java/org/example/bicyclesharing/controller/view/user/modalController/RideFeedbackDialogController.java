package org.example.bicyclesharing.controller.view.user.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.bicyclesharing.util.LocalizationManager;

public class RideFeedbackDialogController {

  @FXML private Label titleLabel;
  @FXML private Label questionLabel;
  @FXML private Button yesButton;
  @FXML private Button noButton;

  private Stage stage;
  private Boolean likedRide = null;

  @FXML
  public void initialize() {
    titleLabel.setText(LocalizationManager.getStringByKey("ride.feedback.title"));
    questionLabel.setText(LocalizationManager.getStringByKey("ride.feedback.question"));
    yesButton.setText(LocalizationManager.getStringByKey("ride.feedback.yes"));
    noButton.setText(LocalizationManager.getStringByKey("ride.feedback.no"));
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public Boolean getLikedRide() {
    return likedRide;
  }

  @FXML
  private void onYes() {
    likedRide = true;
    closeStage();
  }

  @FXML
  private void onNo() {
    likedRide = false;
    closeStage();
  }

  private void closeStage() {
    if (stage != null) {
      stage.close();
    }
  }
}