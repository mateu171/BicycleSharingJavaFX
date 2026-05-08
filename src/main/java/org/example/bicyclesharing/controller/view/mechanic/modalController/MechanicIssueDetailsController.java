package org.example.bicyclesharing.controller.view.mechanic.modalController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.viewModel.mechanic.modalViewModel.MechanicIssueDetailsViewModel;

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
  private final MechanicIssueDetailsViewModel viewModel =
      new MechanicIssueDetailsViewModel();

  @FXML
  public void initialize() {
    bind();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    closeButton.textProperty().bind(viewModel.closeButtonTextProperty());

    bikeLabel.textProperty().bind(viewModel.bikeTextProperty());
    problemLabel.textProperty().bind(viewModel.problemTextProperty());
    commentLabel.textProperty().bind(viewModel.commentTextProperty());
    technicalLabel.textProperty().bind(viewModel.technicalTextProperty());
    statusLabel.textProperty().bind(viewModel.statusTextProperty());
    dateLabel.textProperty().bind(viewModel.dateTextProperty());
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public void setData(
      String bike,
      BikeIssue issue,
      String dateText,
      String statusText,
      String technicalText
  ) {
    viewModel.initialize(
        bike,
        issue,
        dateText,
        statusText,
        technicalText
    );
  }

  @FXML
  private void onClose() {
    if (stage != null) {
      stage.close();
    }
  }
}