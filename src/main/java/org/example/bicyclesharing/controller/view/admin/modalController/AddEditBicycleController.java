package org.example.bicyclesharing.controller.view.admin.modalController;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.*;
import org.example.bicyclesharing.viewModel.admin.modalViewModal.AddEditBicycleViewModel;

public class AddEditBicycleController {

  @FXML private Label titleLabel;
  @FXML private Label modelLabel;
  @FXML private Label typeLabel;
  @FXML private Label priceLabel;
  @FXML private Label stationLabel;
  @FXML private Label photoLabel;
  @FXML private Label photoFileNameLabel;
  @FXML private Label photoErrorLabel;

  @FXML private TextField modelField;
  @FXML private ComboBox<TypeBicycle> typeComboBox;
  @FXML private TextField priceField;
  @FXML private ComboBox<Station> stationComboBox;
  @FXML private ImageView photoPreview;

  @FXML private Label modelErrorLabel;
  @FXML private Label typeErrorLabel;
  @FXML private Label priceErrorLabel;
  @FXML private Label stationErrorLabel;

  @FXML private Button cancelButton;
  @FXML private Button saveButton;
  @FXML private Button uploadPhotoButton;

  private AddEditBicycleViewModel viewModel;
  private Runnable onSaved;

  public void initData(Bicycle bicycle, Runnable onSaved) {
    this.onSaved = onSaved;

    viewModel = new AddEditBicycleViewModel(
        AppConfig.bicycleService(),
        AppConfig.stationService(),
        bicycle
    );

    setupComboBoxes();
    bind();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    modelLabel.textProperty().bind(viewModel.modelLabelTextProperty());
    typeLabel.textProperty().bind(viewModel.typeLabelTextProperty());
    priceLabel.textProperty().bind(viewModel.priceLabelTextProperty());
    stationLabel.textProperty().bind(viewModel.stationLabelTextProperty());
    photoLabel.textProperty().bind(viewModel.photoLabelTextProperty());

    cancelButton.textProperty().bind(viewModel.cancelButtonTextProperty());
    saveButton.textProperty().bind(viewModel.saveButtonTextProperty());
    uploadPhotoButton.textProperty().bind(viewModel.uploadButtonTextProperty());

    modelField.textProperty().bindBidirectional(viewModel.modelProperty());
    priceField.textProperty().bindBidirectional(viewModel.priceProperty());

    typeComboBox.itemsProperty().bind(viewModel.typesProperty());
    stationComboBox.itemsProperty().bind(viewModel.stationsProperty());

    typeComboBox.valueProperty().bindBidirectional(viewModel.selectedTypeProperty());
    stationComboBox.valueProperty().bindBidirectional(viewModel.selectedStationProperty());

    modelErrorLabel.textProperty().bind(viewModel.modelErrorProperty());
    typeErrorLabel.textProperty().bind(viewModel.typeErrorProperty());
    priceErrorLabel.textProperty().bind(viewModel.priceErrorProperty());
    stationErrorLabel.textProperty().bind(viewModel.stationErrorProperty());
    photoErrorLabel.textProperty().bind(viewModel.photoErrorProperty());
    photoFileNameLabel.textProperty().bind(viewModel.photoFileNameTextProperty());

    viewModel.photoPreviewPathProperty().addListener((obs, oldVal, newVal) -> updatePreview(newVal));
    updatePreview(viewModel.photoPreviewPathProperty().get());
  }

  private void setupComboBoxes() {
    typeComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(TypeBicycle type) {
        return type == null ? "" : LocalizationManager.getStringByKey(type.getKey());
      }

      @Override
      public TypeBicycle fromString(String string) {
        return null;
      }
    });

    stationComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Station station) {
        return station == null ? "" : station.getName();
      }

      @Override
      public Station fromString(String string) {
        return null;
      }
    });
  }

  @FXML
  private void onSave() {
    try {
      if (viewModel.save()) {
        if (onSaved != null) {
          onSaved.run();
        }
        close();
      }
    } catch (BusinessException e) {
      DialogUtil.showError(e.getMessage());
    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.save.failed"));
    }
  }

  @FXML
  private void onUploadPhoto() {
    Stage stage = (Stage) cancelButton.getScene().getWindow();
    File file = ImageStorageUtil.chooseImage(stage);

    if (file != null) {
      viewModel.selectPhoto(file);
    }
  }

  @FXML
  private void onClose() {
    close();
  }

  private void close() {
    ((Stage) saveButton.getScene().getWindow()).close();
  }

  private void updatePreview(String path) {
    if (path == null || path.isBlank()) {
      return;
    }

    File file = new File(path);

    if (file.exists()) {
      photoPreview.setImage(new Image(file.toURI().toString()));
      return;
    }

    var resource = getClass().getResource(path);
    if (resource != null) {
      photoPreview.setImage(new Image(resource.toExternalForm()));
    }
  }
}