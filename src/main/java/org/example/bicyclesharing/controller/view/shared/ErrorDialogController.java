package org.example.bicyclesharing.controller.view.shared;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.bicyclesharing.util.LocalizationManager;

public class ErrorDialogController {

  @FXML private Label titleLabel;
  @FXML private Label messageLabel;
  @FXML private Button okButton;

  public void  initData(String messageKey)
  {
    titleLabel.setText(LocalizationManager.getStringByKey("dialog.error.title"));
    okButton.setText(LocalizationManager.getStringByKey("ok.button"));
    messageLabel.setText(LocalizationManager.getStringByKey(messageKey));
  }

  @FXML
  private void onOk()
  {
    close();
  }

  private void close()
  {
    ((Stage) okButton.getScene().getWindow()).close();
  }

}
