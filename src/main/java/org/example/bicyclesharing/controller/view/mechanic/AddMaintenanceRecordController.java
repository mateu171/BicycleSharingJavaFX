package org.example.bicyclesharing.controller.view.mechanic;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.MaintenanceAction;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.mechanic.AddMaintenanceRecordViewModel;

public class AddMaintenanceRecordController extends BaseController {

  @FXML private Label actionLabel;
  @FXML private ComboBox<MaintenanceAction> actionCombo;
  @FXML private Label actionErrorLabel;

  @FXML private Label titleLabel;
  @FXML private Label bikeSectionTitleLabel;
  @FXML private Label recordSectionTitleLabel;
  @FXML private Label typeLabel;
  @FXML private Label descriptionLabel;
  @FXML private Label resultLabel;

  @FXML private Label searchErrorLabel;
  @FXML private Label bicycleErrorLabel;
  @FXML private Label typeErrorLabel;
  @FXML private Label descriptionErrorLabel;
  @FXML private Label resultErrorLabel;
  @FXML private Label successLabel;

  @FXML private TextField searchField;
  @FXML private TableView<Bicycle> bicyclesTable;
  @FXML private TableColumn<Bicycle, String> modelColumn;
  @FXML private TableColumn<Bicycle, String> stateColumn;

  @FXML private ComboBox<MaintenanceType> typeCombo;
  @FXML private TextArea descriptionArea;
  @FXML private TextArea resultArea;
  @FXML private Button saveButton;
  @FXML private Button clearButton;

  private AddMaintenanceRecordViewModel viewModel;

  @FXML
  public void initialize() {
    searchErrorLabel.setVisible(false);
    searchErrorLabel.setManaged(false);

    bindMessageVisibility(bicycleErrorLabel);
    bindMessageVisibility(typeErrorLabel);
    bindMessageVisibility(descriptionErrorLabel);
    bindMessageVisibility(resultErrorLabel);
    bindMessageVisibility(successLabel);
    bindMessageVisibility(actionErrorLabel);

    setupConverters();
  }

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new AddMaintenanceRecordViewModel(
        currentUser,
        AppConfig.bicycleService(),
        AppConfig.maintenanceRecordService()
    );

    setupBindings();
    setupListeners();
  }

  private void setupConverters() {
    typeCombo.getItems().setAll(MaintenanceType.values());
    typeCombo.setConverter(new StringConverter<>() {
      @Override
      public String toString(MaintenanceType type) {
        return type == null ? "" : LocalizationManager.getStringByKey(type.getKey());
      }

      @Override
      public MaintenanceType fromString(String string) {
        return null;
      }
    });

    actionCombo.getItems().setAll(MaintenanceAction.values());
    actionCombo.setConverter(new StringConverter<>() {
      @Override
      public String toString(MaintenanceAction action) {
        return action == null ? "" : LocalizationManager.getStringByKey(action.getKey());
      }

      @Override
      public MaintenanceAction fromString(String string) {
        return null;
      }
    });
  }

  private void setupBindings() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    bikeSectionTitleLabel.textProperty().bind(viewModel.bikeSectionTitleTextProperty());
    recordSectionTitleLabel.textProperty().bind(viewModel.recordSectionTitleTextProperty());
    typeLabel.textProperty().bind(viewModel.typeLabelTextProperty());
    descriptionLabel.textProperty().bind(viewModel.descriptionLabelTextProperty());
    resultLabel.textProperty().bind(viewModel.resultLabelTextProperty());
    actionLabel.textProperty().bind(viewModel.actionTextProperty());

    saveButton.textProperty().bind(viewModel.saveButtonTextProperty());
    clearButton.textProperty().bind(viewModel.clearButtonTextProperty());

    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());

    modelColumn.textProperty().bind(viewModel.modelColumnTextProperty());
    stateColumn.textProperty().bind(viewModel.stateColumnTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
    typeCombo.valueProperty().bindBidirectional(viewModel.selectedTypeProperty());
    descriptionArea.textProperty().bindBidirectional(viewModel.descriptionProperty());
    resultArea.textProperty().bindBidirectional(viewModel.resultProperty());
    actionCombo.valueProperty().bindBidirectional(viewModel.selectedActionProperty());

    modelColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(cell.getValue().getModel()));

    stateColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(viewModel.getStateText(cell.getValue())));

    bicyclesTable.setItems(viewModel.getFilteredBicycles());

    bicycleErrorLabel.textProperty().bind(localizedText(viewModel.bicycleErrorKeyProperty()));
    typeErrorLabel.textProperty().bind(localizedText(viewModel.typeErrorKeyProperty()));
    descriptionErrorLabel.textProperty().bind(localizedText(viewModel.descriptionErrorKeyProperty()));
    resultErrorLabel.textProperty().bind(localizedText(viewModel.resultErrorKeyProperty()));
    actionErrorLabel.textProperty().bind(localizedText(viewModel.actionErrorKeyProperty()));
    successLabel.textProperty().bind(localizedText(viewModel.successMessageKeyProperty()));
  }

  private void setupListeners() {
    bicyclesTable.getSelectionModel().selectedItemProperty().addListener(
        (obs, oldValue, newValue) -> viewModel.selectBicycle(newValue)
    );

    typeCombo.valueProperty().addListener(
        (obs, oldValue, newValue) -> viewModel.clearTypeError()
    );

    descriptionArea.textProperty().addListener(
        (obs, oldValue, newValue) -> viewModel.clearDescriptionError()
    );

    resultArea.textProperty().addListener(
        (obs, oldValue, newValue) -> viewModel.clearResultError()
    );

    actionCombo.valueProperty().addListener(
        (obs, oldValue, newValue) -> viewModel.clearActionError()
    );
  }

  private StringBinding localizedText(StringProperty keyProperty) {
    return Bindings.createStringBinding(
        () -> {
          String key = keyProperty.get();
          return key == null || key.isBlank()
              ? ""
              : LocalizationManager.getStringByKey(key);
        },
        keyProperty,
        LocalizationManager.localeProperty()
    );
  }

  private void bindMessageVisibility(Label label) {
    label.visibleProperty().bind(label.textProperty().isNotEmpty());
    label.managedProperty().bind(label.visibleProperty());
  }

  @FXML
  private void onSave() {
    viewModel.save();
  }

  @FXML
  private void onClear() {
    viewModel.clearForm();
    bicyclesTable.getSelectionModel().clearSelection();
  }
}