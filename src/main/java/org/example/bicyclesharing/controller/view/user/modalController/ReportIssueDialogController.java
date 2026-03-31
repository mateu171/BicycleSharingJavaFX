package org.example.bicyclesharing.controller.view.user.modalController;

import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.bicyclesharing.util.LocalizationManager;

public class ReportIssueDialogController {

  @FXML private Label titleLabel;
  @FXML private Label headerLabel;
  @FXML private Label problemTypeLabel;
  @FXML private Label commentLabel;
  @FXML private Label errorLabel;

  @FXML private ComboBox<String> problemTypeComboBox;
  @FXML private TextArea commentTextArea;

  @FXML private Button cancelButton;
  @FXML private Button submitButton;

  private Stage stage;
  private ReportIssueResult result;

  @FXML
  public void initialize() {
    titleLabel.setText(LocalizationManager.getStringByKey("ride.problem.title"));
    headerLabel.setText(LocalizationManager.getStringByKey("ride.problem.header"));
    problemTypeLabel.setText(LocalizationManager.getStringByKey("ride.problem.select"));
    commentLabel.setText(LocalizationManager.getStringByKey("ride.problem.comment.label"));

    cancelButton.setText(LocalizationManager.getStringByKey("cancel.button"));
    submitButton.setText(LocalizationManager.getStringByKey("save.button"));

    problemTypeComboBox.setItems(FXCollections.observableArrayList(
        LocalizationManager.getStringByKey("ride.problem.type.broken"),
        LocalizationManager.getStringByKey("ride.problem.type.brakes"),
        LocalizationManager.getStringByKey("ride.problem.type.wheel"),
        LocalizationManager.getStringByKey("ride.problem.type.seat"),
        LocalizationManager.getStringByKey("ride.problem.type.uncomfortable"),
        LocalizationManager.getStringByKey("ride.problem.type.other")
    ));

    problemTypeComboBox.getSelectionModel().selectFirst();
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public Optional<ReportIssueResult> getResult() {
    return Optional.ofNullable(result);
  }

  @FXML
  private void onSubmit() {
    String problemType = problemTypeComboBox.getValue();
    String comment = commentTextArea.getText() == null ? "" : commentTextArea.getText().trim();

    if (problemType == null || problemType.isBlank()) {
      showError(LocalizationManager.getStringByKey("ride.problem.validation.type.required"));
      return;
    }

    result = new ReportIssueResult(problemType, comment);
    closeStage();
  }

  @FXML
  private void onClose() {
    result = null;
    closeStage();
  }

  private void showError(String text) {
    errorLabel.setText(text);
    errorLabel.setVisible(true);
    errorLabel.setManaged(true);
  }

  private void closeStage() {
    if (stage != null) {
      stage.close();
    }
  }

  public record ReportIssueResult(String problemType, String comment) {
  }
}
