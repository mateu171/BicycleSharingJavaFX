package org.example.bicyclesharing.controller.view.admin.modalController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.Station;
import org.example.bicyclesharing.domain.enums.TypeBicycle;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.admin.modalViewModal.AddEditBicycleViewModel;

public class AddEditBicycleController {

  @FXML private Label titleLabel;
  @FXML private Label modelLabel;
  @FXML private Label typeLabel;
  @FXML private Label priceLabel;
  @FXML private Label stationLabel;

  @FXML private TextField modelField;
  @FXML private ComboBox<TypeBicycle> typeComboBox;
  @FXML private TextField priceField;
  @FXML private ComboBox<Station> stationComboBox;

  @FXML private Label modelErrorLabel;
  @FXML private Label typeErrorLabel;
  @FXML private Label priceErrorLabel;
  @FXML private Label stationErrorLabel;

  @FXML private Button closeButton;
  @FXML private Button cancelButton;
  @FXML private Button saveButton;

  private AddEditBicycleViewModel viewModel;
  private Runnable onSaved;

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
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    modelLabel.textProperty().bind(viewModel.modelLabelText);
    typeLabel.textProperty().bind(viewModel.typeLabelText);
    priceLabel.textProperty().bind(viewModel.priceLabelText);
    stationLabel.textProperty().bind(viewModel.stationLabelText);

    cancelButton.textProperty().bind(viewModel.cancelButtonText);
    saveButton.textProperty().bind(viewModel.saveButtonText);

    modelField.textProperty().bindBidirectional(viewModel.model);
    priceField.textProperty().bindBidirectional(viewModel.price);

    modelErrorLabel.textProperty().bind(viewModel.modelError);
    typeErrorLabel.textProperty().bind(viewModel.typeError);
    priceErrorLabel.textProperty().bind(viewModel.priceError);
    stationErrorLabel.textProperty().bind(viewModel.stationError);
  }

  @FXML
  private void onSave() {
    viewModel.selectedType = typeComboBox.getValue();
    viewModel.selectedStation = stationComboBox.getValue();

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
}