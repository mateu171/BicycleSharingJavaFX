package org.example.bicyclesharing.controller.view.admin.modalController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.admin.modalViewModal.AddEditStationViewModel;

public class AddEditStationController {

  @FXML private Label titleLabel;
  @FXML private Label nameLabel;
  @FXML private TextField nameField;
  @FXML private Label nameErrorLabel;
  @FXML private Label latitudeErrorLabel;
  @FXML private Button cancelButton;
  @FXML private Button saveButton;
  @FXML private Button pickOnMapButton;
  @FXML private Label locationInfoLabel;

  private AddEditStationViewModel viewModel;
  private Runnable onSaved;

  public void initData(Station station, Runnable onSaved) {
    this.onSaved = onSaved;
    this.viewModel = new AddEditStationViewModel(
        AppConfig.stationService(),
        station
    );

    bind();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    nameLabel.textProperty().bind(viewModel.nameLabelTextProperty());
    cancelButton.textProperty().bind(viewModel.cancelButtonTextProperty());
    saveButton.textProperty().bind(viewModel.saveButtonTextProperty());
    pickOnMapButton.textProperty().bind(viewModel.pickOnMapButtonTextProperty());
    nameField.textProperty().bindBidirectional(viewModel.nameProperty());
    locationInfoLabel.textProperty().bind(viewModel.locationInfoProperty());
    nameErrorLabel.textProperty().bind(viewModel.nameErrorProperty());
    latitudeErrorLabel.textProperty().bind(viewModel.coordinatesErrorProperty());
  }

  @FXML
  private void onSave() {

    if (viewModel.save()) {
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
    ((Stage) saveButton.getScene().getWindow()).close();
  }

  @FXML
  private void onPickOnMap() {
    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/org/example/bicyclesharing/presentation/view/admin/modalView/MapPickerView.fxml")
      );

      Parent root = loader.load();

      MapPickerController controller = loader.getController();
      controller.setListener(viewModel::setCoordinates);

      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(new Scene(root, 900, 600));
      stage.showAndWait();

    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.operation.failed"));
    }
  }
}