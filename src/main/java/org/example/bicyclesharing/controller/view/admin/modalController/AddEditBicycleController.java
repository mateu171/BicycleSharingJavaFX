package org.example.bicyclesharing.controller.view.admin.modalController;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.ImageStorageUtil;
import org.example.bicyclesharing.util.LocalizationManager;
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
  private File selectedImage;

  public void initData(Bicycle bicycle, Runnable onSaved) {
    this.onSaved = onSaved;
    this.viewModel = new AddEditBicycleViewModel(
        AppConfig.bicycleService(),
        AppConfig.stationService(),
        bicycle
    );

    bind();

    typeComboBox.setItems(FXCollections.observableArrayList(TypeBicycle.values()));
    typeComboBox.setValue(viewModel.selectedType);

    typeComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(TypeBicycle item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : LocalizationManager.getStringByKey(item.getKey()));
      }
    });

    typeComboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(TypeBicycle item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : LocalizationManager.getStringByKey(item.getKey()));
      }
    });

    stationComboBox.setItems(viewModel.stations);
    stationComboBox.setValue(viewModel.selectedStation);

    stationComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(Station item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getName());
      }
    });

    stationComboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(Station item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.getName());
      }
    });

    if (viewModel.isEditMode()) {
      modelField.setPromptText(bicycle.getModel());
      priceField.setPromptText(String.valueOf(bicycle.getPricePerMinute()));
    }

    var defaultImageUrl = getClass().getResource("/org/example/bicyclesharing/art/image/defaultImg.jpg");

    Image image = new Image(defaultImageUrl.toExternalForm());

    if (bicycle != null && bicycle.getImagePath() != null) {
      File file = new File(bicycle.getImagePath());
      if (file.exists()) {
        image = new Image(file.toURI().toString());
      }
    }

    photoPreview.setImage(image);
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    modelLabel.textProperty().bind(viewModel.modelLabelText);
    typeLabel.textProperty().bind(viewModel.typeLabelText);
    priceLabel.textProperty().bind(viewModel.priceLabelText);
    stationLabel.textProperty().bind(viewModel.stationLabelText);
    photoLabel.textProperty().bind(viewModel.photoLabelText);

    cancelButton.textProperty().bind(viewModel.cancelButtonText);
    saveButton.textProperty().bind(viewModel.saveButtonText);
    uploadPhotoButton.textProperty().bind(viewModel.uploadButtonText);

    modelField.textProperty().bindBidirectional(viewModel.model);
    priceField.textProperty().bindBidirectional(viewModel.price);

    modelErrorLabel.textProperty().bind(viewModel.modelError);
    typeErrorLabel.textProperty().bind(viewModel.typeError);
    priceErrorLabel.textProperty().bind(viewModel.priceError);
    stationErrorLabel.textProperty().bind(viewModel.stationError);
    photoErrorLabel.textProperty().bind(viewModel.photoError);
    photoFileNameLabel.textProperty().bind(viewModel.photoFileNameText);
  }

  @FXML
  private void onSave() {
    viewModel.selectedType = typeComboBox.getValue();
    viewModel.selectedStation = stationComboBox.getValue();

    try {
      String imagePath = ImageStorageUtil.saveImage(selectedImage, "bicycles");
      viewModel.setImagePath(imagePath);

      if (viewModel.save()) {
        if (onSaved != null) {
          onSaved.run();
        }
        close();
      }
    } catch (BusinessException e) {
      DialogUtil.showError(e.getMessage());
    } catch (Exception e) {
      DialogUtil.showError("error.save.failed");
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
  private void onUploadPhoto() {
    Stage stage = (Stage) cancelButton.getScene().getWindow();
    File file = ImageStorageUtil.chooseImage(stage);

    if (file != null) {
      selectedImage = file;
      viewModel.photoFileNameText.set(file.getName());
      ImageStorageUtil.showPreview(file, photoPreview, 90, 90);
      viewModel.setPhotoError("");
    }
  }
}