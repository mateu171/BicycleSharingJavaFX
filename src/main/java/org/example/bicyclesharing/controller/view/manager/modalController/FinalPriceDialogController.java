package org.example.bicyclesharing.controller.view.manager.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.bicyclesharing.util.LocalizationManager;

public class FinalPriceDialogController {

  @FXML private Label titleLabel;
  @FXML private Label headerLabel;
  @FXML private Label priceLabel;
  @FXML private Button okButton;

  public void initData(double finalPrice) {
    titleLabel.setText(LocalizationManager.getStringByKey("manager.rentals.finish.success.title"));
    headerLabel.setText(LocalizationManager.getStringByKey("manager.rentals.finish.success.header"));
    okButton.setText(LocalizationManager.getStringByKey("button.ok"));

    priceLabel.setText(
        String.format("%.2f", finalPrice) + " " +
            LocalizationManager.getStringByKey("label.currency")
    );
  }

  @FXML
  private void onClose() {
    ((Stage) okButton.getScene().getWindow()).close();
  }
}