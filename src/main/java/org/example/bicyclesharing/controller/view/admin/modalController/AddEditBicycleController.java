package org.example.bicyclesharing.controller.view.admin.modalController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.viewModel.admin.modalViewModal.AddEditBicycleViewModel;

public class AddEditBicycleController {

  @FXML private Label titleLabel;
  @FXML private Label modelLabel;
  @FXML private Label typeLabel;
  @FXML private Label priceLabel;
  @FXML private Label latitudeLabel;
  @FXML private Label longitudeLabel;

  @FXML private TextField modelField;
  @FXML private ComboBox<TypeBicycle> typeComboBox;
  @FXML private TextField priceField;
  @FXML private TextField latitudeField;
  @FXML private TextField longitudeField;

  @FXML private Label modelErrorLabel;
  @FXML private Label typeErrorLabel;
  @FXML private Label priceErrorLabel;
  @FXML private Label latitudeErrorLabel;
  @FXML private Label longitudeErrorLabel;

  @FXML private Button closeButton;
  @FXML private Button cancelButton;
  @FXML private Button saveButton;

  private AddEditBicycleViewModel viewModel;
  private Runnable onSaved;

  public void initData(Bicycle bicycle, Runnable onSaved) {
    this.onSaved = onSaved;
    this.viewModel = new AddEditBicycleViewModel(AppConfig.bicycleService(), bicycle);
    bind();

    typeComboBox.setItems(FXCollections.observableArrayList(TypeBicycle.values()));
    typeComboBox.setValue(viewModel.selectedType);

    typeComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(TypeBicycle item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getName());
      }
    });

    typeComboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(TypeBicycle item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getName());
      }
    });

    if (viewModel.isEditMode()) {
      modelField.setPromptText(bicycle.getModel());
      priceField.setPromptText(String.valueOf(bicycle.getPricePerMinute()));
      latitudeField.setPromptText(String.valueOf(bicycle.getLatitude()));
      longitudeField.setPromptText(String.valueOf(bicycle.getLongitude()));
    }
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    modelLabel.textProperty().bind(viewModel.modelLabelText);
    typeLabel.textProperty().bind(viewModel.typeLabelText);
    priceLabel.textProperty().bind(viewModel.priceLabelText);
    latitudeLabel.textProperty().bind(viewModel.latitudeLabelText);
    longitudeLabel.textProperty().bind(viewModel.longitudeLabelText);

    cancelButton.textProperty().bind(viewModel.cancelButtonText);
    saveButton.textProperty().bind(viewModel.saveButtonText);

    modelField.textProperty().bindBidirectional(viewModel.model);
    priceField.textProperty().bindBidirectional(viewModel.price);
    latitudeField.textProperty().bindBidirectional(viewModel.latitude);
    longitudeField.textProperty().bindBidirectional(viewModel.longitude);

    modelErrorLabel.textProperty().bind(viewModel.modelError);
    typeErrorLabel.textProperty().bind(viewModel.typeError);
    priceErrorLabel.textProperty().bind(viewModel.priceError);
    latitudeErrorLabel.textProperty().bind(viewModel.latitudeError);
    longitudeErrorLabel.textProperty().bind(viewModel.longitudeError);
  }

  @FXML
  private void onSave() {
    viewModel.selectedType = typeComboBox.getValue();
    if (viewModel.save()) {
      if (onSaved != null) onSaved.run();
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
}