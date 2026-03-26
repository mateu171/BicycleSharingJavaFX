package org.example.bicyclesharing.controller.view.admin;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.admin.AddBicycleViewModel;

public class AddBicycleController {

  @FXML private TextField modelField;
  @FXML private TextField priceField;
  @FXML private TextField latitudeField;
  @FXML private TextField longitudeField;

  @FXML private Label modelErrorLabel;
  @FXML private Label priceErrorLabel;
  @FXML private Label latitudeErrorLabel;
  @FXML private Label longitudeErrorLabel;

  @FXML private Button saveButton;
  @FXML private Button cancelButton;

  private AddBicycleViewModel viewModel;
  private Runnable onBikeAdded;

  @FXML
  private void  initialize()
  {
    viewModel = new AddBicycleViewModel(AppConfig.bicycleService());

    saveButton.textProperty().bind(viewModel.saveButtonText);
    cancelButton.textProperty().bind(viewModel.cancelButtonText);

    modelField.textProperty().bindBidirectional(viewModel.model);
    priceField.textProperty().bindBidirectional(viewModel.price);
    latitudeField.textProperty().bindBidirectional(viewModel.latitude);
    longitudeField.textProperty().bindBidirectional(viewModel.longitude);

    modelField.promptTextProperty().bind(viewModel.modelPromText);
    priceField.promptTextProperty().bind(viewModel.pricePromText);
    latitudeField.promptTextProperty().bind(viewModel.latitudePromText);
    longitudeField.promptTextProperty().bind(viewModel.longitudePromText);

    modelErrorLabel.textProperty().bind(
        Bindings.createStringBinding(
            () -> LocalizationManager.getStringByKey(viewModel.modelErrorKey.get()),
                viewModel.modelErrorKey,
                LocalizationManager.localeProperty()
        )
    );
    priceErrorLabel.textProperty().bind(
        Bindings.createStringBinding(
            () -> LocalizationManager.getStringByKey(viewModel.priceErrorKey.get()),
            viewModel.priceErrorKey,
            LocalizationManager.localeProperty()
        )
    );
    latitudeErrorLabel.textProperty().bind(
        Bindings.createStringBinding(
            () -> LocalizationManager.getStringByKey(viewModel.latitudeErrorKey.get()),
            viewModel.latitudeErrorKey,
            LocalizationManager.localeProperty()
        )
    );
    longitudeErrorLabel.textProperty().bind(
        Bindings.createStringBinding(
            () -> LocalizationManager.getStringByKey(viewModel.longitudeErrorKey.get()),
            viewModel.longitudeErrorKey,
            LocalizationManager.localeProperty()
        )
    );
  }

  public void setOnBikeAdded(Runnable onBikeAdded)
  {
    this.onBikeAdded = onBikeAdded;
  }

  @FXML
  private void onSave()
  {
    boolean success = viewModel.save();

    if(success)
    {
      if(onBikeAdded != null)
      {
        onBikeAdded.run();
      }
      closeWindow();
    }
  }

  @FXML
  private void onCancel() {
    closeWindow();
  }

  private void closeWindow() {
    Stage stage = (Stage) saveButton.getScene().getWindow();
    stage.close();
  }
}
