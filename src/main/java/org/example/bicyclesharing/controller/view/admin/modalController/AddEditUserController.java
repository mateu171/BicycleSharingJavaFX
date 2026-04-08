package org.example.bicyclesharing.controller.view.admin.modalController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.admin.modalViewModal.AddEditUserViewModel;

public class AddEditUserController {

  @FXML private Label titleLabel;
  @FXML private Label loginLabel;
  @FXML private Label passwordLabel;
  @FXML private Label emailLabel;
  @FXML private Label roleLabel;
  @FXML private Label codeLabel;
  @FXML private Label codeInfoLabel;

  @FXML private TextField loginField;
  @FXML private PasswordField passwordField;
  @FXML private TextField emailField;
  @FXML private ComboBox<Role> roleComboBox;
  @FXML private TextField codeField;

  @FXML private Label loginErrorLabel;
  @FXML private Label passwordErrorLabel;
  @FXML private Label emailErrorLabel;
  @FXML private Label roleErrorLabel;
  @FXML private Label codeErrorLabel;

  @FXML private Button backButton;
  @FXML private Button cancelButton;
  @FXML private Button sendCodeButton;
  @FXML private Button saveButton;

  @FXML private VBox userFormBlock;
  @FXML private VBox codeFormBlock;

  private Runnable onSaved;
  private AddEditUserViewModel viewModel;

  public void initData(User user, Runnable onSaved) {
    this.onSaved = onSaved;
    this.viewModel = new AddEditUserViewModel(
        AppConfig.userService(),
        AppConfig.verificationService(),
        user
    );

    bind();
    setupRoleCombo();

    if (viewModel.isEditMode()) {
      roleComboBox.setValue(user.getRole());
    }
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleText);
    loginLabel.textProperty().bind(viewModel.loginLabelText);
    passwordLabel.textProperty().bind(viewModel.passwordLabelText);
    emailLabel.textProperty().bind(viewModel.emailLabelText);
    roleLabel.textProperty().bind(viewModel.roleLabelText);
    codeLabel.textProperty().bind(viewModel.codeLabelText);
    codeInfoLabel.textProperty().bind(viewModel.codeInfoText);

    loginField.textProperty().bindBidirectional(viewModel.login);
    passwordField.textProperty().bindBidirectional(viewModel.password);
    emailField.textProperty().bindBidirectional(viewModel.email);
    codeField.textProperty().bindBidirectional(viewModel.code);

    loginErrorLabel.textProperty().bind(viewModel.loginError);
    passwordErrorLabel.textProperty().bind(viewModel.passwordError);
    emailErrorLabel.textProperty().bind(viewModel.emailError);
    roleErrorLabel.textProperty().bind(viewModel.roleError);
    codeErrorLabel.textProperty().bind(viewModel.codeError);

    saveButton.textProperty().bind(viewModel.saveButtonText);
    cancelButton.textProperty().bind(viewModel.cancelButtonText);
    sendCodeButton.textProperty().bind(viewModel.sendCodeButtonText);
    backButton.textProperty().bind(viewModel.backButtonText);

    userFormBlock.visibleProperty().bind(viewModel.codeStep.not());
    userFormBlock.managedProperty().bind(userFormBlock.visibleProperty());

    codeFormBlock.visibleProperty().bind(viewModel.codeStep);
    codeFormBlock.managedProperty().bind(codeFormBlock.visibleProperty());

    backButton.visibleProperty().bind(viewModel.codeStep);
    backButton.managedProperty().bind(backButton.visibleProperty());

    if (viewModel.isEditMode()) {
      sendCodeButton.setVisible(false);
      sendCodeButton.setManaged(false);

      saveButton.setVisible(true);
      saveButton.setManaged(true);
    } else {
      sendCodeButton.visibleProperty().bind(viewModel.codeStep.not());
      sendCodeButton.managedProperty().bind(sendCodeButton.visibleProperty());

      saveButton.visibleProperty().bind(viewModel.codeStep);
      saveButton.managedProperty().bind(saveButton.visibleProperty());
    }

    bindErrorVisibility(loginErrorLabel);
    bindErrorVisibility(passwordErrorLabel);
    bindErrorVisibility(emailErrorLabel);
    bindErrorVisibility(roleErrorLabel);
    bindErrorVisibility(codeErrorLabel);
  }

  private void setupRoleCombo() {
    roleComboBox.setItems(FXCollections.observableArrayList(
        Role.ADMIN,
        Role.MANAGER,
        Role.MECHANIC
    ));

    roleComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(Role item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : LocalizationManager.getStringByKey(item.getKey()));
      }
    });

    roleComboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(Role item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : LocalizationManager.getStringByKey(item.getKey()));
      }
    });

    roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> viewModel.setSelectedRole(newVal));
  }

  private void bindErrorVisibility(Label label) {
    label.visibleProperty().bind(label.textProperty().isNotEmpty());
    label.managedProperty().bind(label.visibleProperty());
  }

  @FXML
  private void onSendCode() {
    if (viewModel.sendCode()) {
      viewModel.goToCodeStep();
    }
  }

  @FXML
  private void onBack() {
    viewModel.goToFormStep();
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
    ((Stage) cancelButton.getScene().getWindow()).close();
  }
}