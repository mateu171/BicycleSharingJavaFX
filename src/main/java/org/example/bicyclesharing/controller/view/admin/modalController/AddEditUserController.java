package org.example.bicyclesharing.controller.view.admin.modalController;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.ImageStorageUtil;
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
  @FXML private Label photoLabel;
  @FXML private Label photoFileNameLabel;
  @FXML private Label photoErrorLabel;

  @FXML private TextField loginField;
  @FXML private PasswordField passwordField;
  @FXML private TextField emailField;
  @FXML private ComboBox<Role> roleComboBox;
  @FXML private TextField codeField;
  @FXML private ImageView photoPreview;

  @FXML private Label loginErrorLabel;
  @FXML private Label passwordErrorLabel;
  @FXML private Label emailErrorLabel;
  @FXML private Label roleErrorLabel;
  @FXML private Label codeErrorLabel;

  @FXML private Button backButton;
  @FXML private Button cancelButton;
  @FXML private Button sendCodeButton;
  @FXML private Button saveButton;
  @FXML private Button uploadPhotoButton;

  @FXML private VBox userFormBlock;
  @FXML private VBox codeFormBlock;

  private AddEditUserViewModel viewModel;
  private Runnable onSaved;

  public void initData(User user, Runnable onSaved) {
    this.onSaved = onSaved;

    viewModel = new AddEditUserViewModel(
        AppConfig.userService(),
        AppConfig.verificationService(),
        user
    );

    setupRoleComboBox();
    bind();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());

    loginLabel.textProperty().bind(viewModel.loginLabelTextProperty());
    passwordLabel.textProperty().bind(viewModel.passwordLabelTextProperty());
    emailLabel.textProperty().bind(viewModel.emailLabelTextProperty());
    roleLabel.textProperty().bind(viewModel.roleLabelTextProperty());
    codeLabel.textProperty().bind(viewModel.codeLabelTextProperty());
    codeInfoLabel.textProperty().bind(viewModel.codeInfoTextProperty());
    photoLabel.textProperty().bind(viewModel.photoLabelTextProperty());

    loginField.textProperty().bindBidirectional(viewModel.loginProperty());
    passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
    emailField.textProperty().bindBidirectional(viewModel.emailProperty());
    codeField.textProperty().bindBidirectional(viewModel.codeProperty());

    roleComboBox.itemsProperty().bind(viewModel.rolesProperty());
    roleComboBox.valueProperty().bindBidirectional(viewModel.selectedRoleProperty());

    loginErrorLabel.textProperty().bind(viewModel.loginErrorProperty());
    passwordErrorLabel.textProperty().bind(viewModel.passwordErrorProperty());
    emailErrorLabel.textProperty().bind(viewModel.emailErrorProperty());
    roleErrorLabel.textProperty().bind(viewModel.roleErrorProperty());
    codeErrorLabel.textProperty().bind(viewModel.codeErrorProperty());
    photoErrorLabel.textProperty().bind(viewModel.photoErrorProperty());
    photoFileNameLabel.textProperty().bind(viewModel.photoFileNameTextProperty());

    saveButton.textProperty().bind(viewModel.saveButtonTextProperty());
    cancelButton.textProperty().bind(viewModel.cancelButtonTextProperty());
    sendCodeButton.textProperty().bind(viewModel.sendCodeButtonTextProperty());
    backButton.textProperty().bind(viewModel.backButtonTextProperty());
    uploadPhotoButton.textProperty().bind(viewModel.uploadButtonTextProperty());

    userFormBlock.visibleProperty().bind(viewModel.codeStepProperty().not());
    userFormBlock.managedProperty().bind(userFormBlock.visibleProperty());

    codeFormBlock.visibleProperty().bind(viewModel.codeStepProperty());
    codeFormBlock.managedProperty().bind(codeFormBlock.visibleProperty());

    backButton.visibleProperty().bind(viewModel.codeStepProperty());
    backButton.managedProperty().bind(backButton.visibleProperty());

    if (viewModel.isEditMode()) {
      sendCodeButton.setVisible(false);
      sendCodeButton.setManaged(false);

      saveButton.setVisible(true);
      saveButton.setManaged(true);
    } else {
      sendCodeButton.visibleProperty().bind(viewModel.codeStepProperty().not());
      sendCodeButton.managedProperty().bind(sendCodeButton.visibleProperty());

      saveButton.visibleProperty().bind(viewModel.codeStepProperty());
      saveButton.managedProperty().bind(saveButton.visibleProperty());
    }

    loginField.disableProperty().bind(viewModel.sendingCodeProperty());
    passwordField.disableProperty().bind(viewModel.sendingCodeProperty());
    emailField.disableProperty().bind(viewModel.sendingCodeProperty());
    roleComboBox.disableProperty().bind(viewModel.sendingCodeProperty());
    codeField.disableProperty().bind(viewModel.sendingCodeProperty());

    sendCodeButton.disableProperty().bind(viewModel.sendingCodeProperty());
    saveButton.disableProperty().bind(viewModel.sendingCodeProperty());
    backButton.disableProperty().bind(viewModel.sendingCodeProperty());
    cancelButton.disableProperty().bind(viewModel.sendingCodeProperty());
    uploadPhotoButton.disableProperty().bind(viewModel.sendingCodeProperty());

    viewModel.photoPreviewPathProperty().addListener((obs, oldVal, newVal) -> updatePreview(newVal));
    updatePreview(viewModel.photoPreviewPathProperty().get());
  }

  private void setupRoleComboBox() {
    roleComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Role role) {
        return role == null ? "" : LocalizationManager.getStringByKey(role.getKey());
      }

      @Override
      public Role fromString(String string) {
        return null;
      }
    });
  }

  @FXML
  private void onSendCode() {
    viewModel.sendCodeAsync();
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
  private void onUploadPhoto() {
    if (viewModel.sendingCodeProperty().get()) {
      return;
    }

    Stage stage = (Stage) cancelButton.getScene().getWindow();
    File file = ImageStorageUtil.chooseImage(stage);

    if (file != null) {
      viewModel.selectPhoto(file);
    }
  }

  @FXML
  private void onClose() {
    if (!viewModel.sendingCodeProperty().get()) {
      close();
    }
  }

  private void close() {
    ((Stage) cancelButton.getScene().getWindow()).close();
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