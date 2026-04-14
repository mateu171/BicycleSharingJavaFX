package org.example.bicyclesharing.controller.view.manager.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
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

  public void initData(User currentUser, Rental rental, Runnable onSaved) {
    this.viewModel = new FinishRentalDialogViewModel(
        currentUser,
        rental,
        AppConfig.rentalService(),
        AppConfig.customerService(),
        AppConfig.bicycleService(),
        AppConfig.bikeIssueService()
    );
    this.onSaved = onSaved;
    bind();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);

    problemCheckBox.textProperty().bind(viewModel.problemText);
    technicalProblemCheckBox.textProperty().bind(viewModel.technicalProblemText);

    problemTypeLabel.textProperty().bind(viewModel.problemTypeLabelText);
    commentLabel.textProperty().bind(viewModel.commentLabelText);

    problemCheckBox.selectedProperty().bindBidirectional(viewModel.hasProblem);
    technicalProblemCheckBox.selectedProperty().bindBidirectional(viewModel.technicalProblem);

    problemTypeCombo.getItems().setAll(viewModel.getProblemTypes());
    problemTypeCombo.valueProperty().bindBidirectional(viewModel.selectedProblemType);

    commentArea.textProperty().bindBidirectional(viewModel.comment);

    problemTypeErrorLabel.textProperty().bind(viewModel.problemTypeError);
    commentErrorLabel.textProperty().bind(viewModel.commentError);

    cancelButton.textProperty().bind(viewModel.cancelButtonText);
    finishButton.textProperty().bind(viewModel.finishButtonText);

    problemTypeCombo.disableProperty().bind(viewModel.hasProblem.not());
    commentArea.disableProperty().bind(viewModel.hasProblem.not());
    technicalProblemCheckBox.disableProperty().bind(viewModel.hasProblem.not());

    problemCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
      if (!newVal) {
        problemTypeCombo.getSelectionModel().clearSelection();
        technicalProblemCheckBox.setSelected(false);
        commentArea.clear();
        viewModel.problemTypeError.set("");
        viewModel.commentError.set("");
      }
    });

    problemTypeCombo.valueProperty().addListener((obs, oldVal, newVal) ->
        viewModel.problemTypeError.set("")
    );

    commentArea.textProperty().addListener((obs, oldVal, newVal) ->
        viewModel.commentError.set("")
    );

    bindErrorVisibility(problemTypeErrorLabel);
    bindErrorVisibility(commentErrorLabel);
  }

  private void bindErrorVisibility(Label label) {
    label.visibleProperty().bind(label.textProperty().isNotEmpty());
    label.managedProperty().bind(label.visibleProperty());
  }

  @FXML
  private void onFinish() {
    if (viewModel.finishRental()) {
      if (onSaved != null) {
        onSaved.run();
      }
      close();
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