package org.example.bicyclesharing.controller.view.mechanic.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.util.LocalizationManager;

public class MechanicIssueDetailsController {

  @FXML private Label titleLabel;
  @FXML private Label bikeLabel;
  @FXML private Label problemLabel;
  @FXML private Label commentLabel;
  @FXML private Label technicalLabel;
  @FXML private Label statusLabel;
  @FXML private Label dateLabel;
  @FXML private Button closeButton;

  private Stage stage;

  @FXML
  public void initialize() {
    titleLabel.setText(LocalizationManager.getStringByKey("mechanic.issue.details.title"));
    closeButton.setText(LocalizationManager.getStringByKey("exit.button"));
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public void setData(String bike, BikeIssue issue, String dateText, String statusText, String technicalText) {
    bikeLabel.setText(LocalizationManager.getStringByKey("mechanic.issue.details.bike") + ": " + bike);
    problemLabel.setText(LocalizationManager.getStringByKey("mechanic.issue.details.problem") + ": " + issue.getProblemType());
    commentLabel.setText(LocalizationManager.getStringByKey("mechanic.issue.details.comment") + ": " +
        (issue.getComment() == null || issue.getComment().isBlank() ? "-" : issue.getComment()));
    technicalLabel.setText(LocalizationManager.getStringByKey("mechanic.issue.details.technical") + ": " + technicalText);
    statusLabel.setText(LocalizationManager.getStringByKey("mechanic.issue.details.status") + ": " + statusText);
    dateLabel.setText(LocalizationManager.getStringByKey("mechanic.issue.details.date") + ": " + dateText);
  }

  @FXML
  private void onClose()
  {
    if(stage != null)
      stage.close();
  }
}
