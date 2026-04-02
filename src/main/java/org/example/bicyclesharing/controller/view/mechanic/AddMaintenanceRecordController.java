package org.example.bicyclesharing.controller.view.mechanic;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.MaintenanceAction;
import org.example.bicyclesharing.domain.enums.MaintenanceType;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.mechanic.AddMaintenanceRecordViewModel;

public class AddMaintenanceRecordController extends BaseController {

  @FXML private Label actionLabel;
  @FXML private ComboBox actionCombo;
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
      public String toString(Object o) {
        return (MaintenanceAction)o == null ? "" :
            LocalizationManager.getStringByKey(((MaintenanceAction) o).getKey());
      }

      @Override
      public MaintenanceAction fromString(String string) {
        return null;
      }
    });
    modelColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(cell.getValue().getModel()));

    typeCombo.valueProperty().addListener((obs, oldValue, newValue) -> viewModel.typeErrorKey.set(""));
    descriptionArea.textProperty().addListener((obs, oldValue, newValue) -> viewModel.descriptionErrorKey.set(""));
    resultArea.textProperty().addListener((obs, oldValue, newValue) -> viewModel.resultErrorKey.set(""));
  }

  private StringBinding localizedText(javafx.beans.property.StringProperty keyProperty) {
    return Bindings.createStringBinding(
        () -> LocalizationManager.getStringByKey(keyProperty.get()),
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

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new AddMaintenanceRecordViewModel(currentUser);
    setupBindings();
  }

  private void setupBindings() {
    titleLabel.textProperty().bind(viewModel.titleText);
    bikeSectionTitleLabel.textProperty().bind(viewModel.bikeSectionTitleText);
    recordSectionTitleLabel.textProperty().bind(viewModel.recordSectionTitleText);
    typeLabel.textProperty().bind(viewModel.typeLabelText);
    descriptionLabel.textProperty().bind(viewModel.descriptionLabelText);
    resultLabel.textProperty().bind(viewModel.resultLabelText);
    actionLabel.textProperty().bind(viewModel.actionText);

    saveButton.textProperty().bind(viewModel.saveButtonText);
    clearButton.textProperty().bind(viewModel.clearButtonText);

    searchField.promptTextProperty().bind(viewModel.searchPromptText);

    modelColumn.textProperty().bind(viewModel.modelColumnText);
    stateColumn.textProperty().bind(viewModel.stateColumnText);

    searchField.textProperty().bindBidirectional(viewModel.searchText);
    typeCombo.valueProperty().bindBidirectional(viewModel.selectedType);
    descriptionArea.textProperty().bindBidirectional(viewModel.description);
    resultArea.textProperty().bindBidirectional(viewModel.result);

    stateColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(viewModel.getStateText(cell.getValue())));

    FilteredList<Bicycle> filtered = viewModel.getFilteredBicycles();
    bicyclesTable.setItems(filtered);

    bicyclesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
      viewModel.selectedBicycle.set(newValue);
      viewModel.bicycleErrorKey.set("");
    });
    actionCombo.valueProperty().bindBidirectional(viewModel.selectedAction);
    actionErrorLabel.textProperty().bind(
        localizedText(viewModel.actionErrorKey)
    );

    bicycleErrorLabel.textProperty().bind(localizedText(viewModel.bicycleErrorKey));
    typeErrorLabel.textProperty().bind(localizedText(viewModel.typeErrorKey));
    descriptionErrorLabel.textProperty().bind(localizedText(viewModel.descriptionErrorKey));
    resultErrorLabel.textProperty().bind(localizedText(viewModel.resultErrorKey));
    actionErrorLabel.textProperty().bind(localizedText(viewModel.actionErrorKey));
    successLabel.textProperty().bind(localizedText(viewModel.successMessageKey));
  }
}