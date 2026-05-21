package org.example.bicyclesharing.controller.view.manager.modalController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.manager.modalViewModal.FinishRentalDialogViewModel;

public class FinishRentalDialogController {

  @FXML private Label titleLabel;

  @FXML private CheckBox problemCheckBox;
  @FXML private CheckBox technicalProblemCheckBox;

  @FXML private Label problemTypeLabel;
  @FXML private ComboBox<String> problemTypeCombo;
  @FXML private Label problemTypeErrorLabel;

  @FXML private Label commentLabel;
  @FXML private TextArea commentArea;
  @FXML private Label commentErrorLabel;

  @FXML private Button cancelButton;
  @FXML private Button finishButton;

  private Runnable onSaved;
  private FinishRentalDialogViewModel viewModel;

  public void initData(Rental rental, Runnable onSaved) {
    this.viewModel = new FinishRentalDialogViewModel(
        rental,
        AppConfig.rentalService()
    );
    this.onSaved = onSaved;
    bind();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());

    problemCheckBox.textProperty().bind(viewModel.problemTextProperty());
    technicalProblemCheckBox.textProperty().bind(viewModel.technicalProblemTextProperty());

    problemTypeLabel.textProperty().bind(viewModel.problemTypeLabelTextProperty());
    commentLabel.textProperty().bind(viewModel.commentLabelTextProperty());

    problemCheckBox.selectedProperty().bindBidirectional(viewModel.hasProblemProperty());
    technicalProblemCheckBox.selectedProperty().bindBidirectional(viewModel.technicalProblemProperty());

    problemTypeCombo.getItems().setAll(viewModel.getProblemTypes());
    problemTypeCombo.valueProperty().bindBidirectional(viewModel.selectedProblemTypeProperty());

    commentArea.textProperty().bindBidirectional(viewModel.commentProperty());

    problemTypeErrorLabel.textProperty().bind(viewModel.problemTypeErrorProperty());
    commentErrorLabel.textProperty().bind(viewModel.commentErrorProperty());

    cancelButton.textProperty().bind(viewModel.cancelButtonTextProperty());
    finishButton.textProperty().bind(viewModel.finishButtonTextProperty());

    problemTypeCombo.disableProperty().bind(viewModel.hasProblemProperty().not());
    commentArea.disableProperty().bind(viewModel.hasProblemProperty().not());
    technicalProblemCheckBox.disableProperty().bind(viewModel.hasProblemProperty().not());

    bindErrorVisibility(problemTypeErrorLabel);
    bindErrorVisibility(commentErrorLabel);
  }

  private void bindErrorVisibility(Label label) {
    label.visibleProperty().bind(label.textProperty().isNotEmpty());
    label.managedProperty().bind(label.visibleProperty());
  }

  @FXML
  private void onFinish() {
    try {
      if (viewModel.finishRental()) {
        if (onSaved != null) {
          onSaved.run();
        }

        showFinalPrice(viewModel.getFinalPrice());
        close();
      }
    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.rental.finish.failed"));
    }
  }

  private void showFinalPrice(double finalPrice) {
    try {
      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/manager/modalView/FinalPriceDialog.fxml",
          (FinalPriceDialogController controller) ->
              controller.initData(finalPrice)
      );
    } catch (Exception e) {
      DialogUtil.showError(
          LocalizationManager.getStringByKey("error.rental.finish.failed")
      );
    }
  }

  @FXML
  private void onClose() {
    close();
  }

  private void close() {
    ((Stage) finishButton.getScene().getWindow()).close();
  }
}