package org.example.bicyclesharing.viewModel.admin.modalViewModal;

import java.io.File;
import java.util.stream.Collectors;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.services.VerificationService;
import org.example.bicyclesharing.util.ImageStorageUtil;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditUserViewModel {

  private static final String DEFAULT_IMAGE =
      "/org/example/bicyclesharing/art/image/defaultImg.jpg";

  private final UserService userService;
  private final VerificationService verificationService;
  private final User editingUser;

  private final StringProperty titleText = new SimpleStringProperty();

  private final StringProperty saveButtonText = LocalizationManager.getStringProperty("save.button");
  private final StringProperty cancelButtonText = LocalizationManager.getStringProperty("cancel.button");
  private final StringProperty uploadButtonText = LocalizationManager.getStringProperty("uploadPhoto.button.text");
  private final StringProperty sendCodeButtonText = LocalizationManager.getStringProperty("button.send.code");
  private final StringProperty backButtonText = LocalizationManager.getStringProperty("button.back");
  private final StringProperty codeInfoText = LocalizationManager.getStringProperty("verification.code.info");

  private final StringProperty loginLabelText = LocalizationManager.getStringProperty("register.login");
  private final StringProperty passwordLabelText = LocalizationManager.getStringProperty("register.password");
  private final StringProperty emailLabelText = LocalizationManager.getStringProperty("register.email");
  private final StringProperty roleLabelText = LocalizationManager.getStringProperty("admin.users.role");
  private final StringProperty codeLabelText = LocalizationManager.getStringProperty("register.code");
  private final StringProperty photoLabelText = LocalizationManager.getStringProperty("admin.users.photo");
  private final StringProperty photoFileNameText = LocalizationManager.getStringProperty("file.not.selected");

  private final StringProperty login = new SimpleStringProperty("");
  private final StringProperty password = new SimpleStringProperty("");
  private final StringProperty email = new SimpleStringProperty("");
  private final StringProperty code = new SimpleStringProperty("");

  private final StringProperty loginError = new SimpleStringProperty("");
  private final StringProperty passwordError = new SimpleStringProperty("");
  private final StringProperty emailError = new SimpleStringProperty("");
  private final StringProperty roleError = new SimpleStringProperty("");
  private final StringProperty codeError = new SimpleStringProperty("");
  private final StringProperty photoError = new SimpleStringProperty("");

  private final BooleanProperty codeStep = new SimpleBooleanProperty(false);
  private final BooleanProperty sendingCode = new SimpleBooleanProperty(false);

  private final ObjectProperty<Role> selectedRole = new SimpleObjectProperty<>();

  private final ObjectProperty<ObservableList<Role>> roles =
      new SimpleObjectProperty<>(FXCollections.observableArrayList(
          Role.ADMIN,
          Role.MANAGER,
          Role.MECHANIC
      ));

  private final StringProperty photoPreviewPath = new SimpleStringProperty(DEFAULT_IMAGE);

  private int sentCode;
  private User pendingUser;
  private File selectedPhotoFile;
  private String imagePath;

  public AddEditUserViewModel(
      UserService userService,
      VerificationService verificationService,
      User editingUser
  ) {
    this.userService = userService;
    this.verificationService = verificationService;
    this.editingUser = editingUser;
  }

  public void initialize() {
    if (isEditMode()) {
      initializeEditMode();
    } else {
      initializeAddMode();
    }
  }

  private void initializeAddMode() {
    titleText.set(LocalizationManager.getStringByKey("admin.users.add.title"));
    selectedRole.set(Role.MANAGER);
    photoPreviewPath.set(DEFAULT_IMAGE);
  }

  private void initializeEditMode() {
    titleText.set(LocalizationManager.getStringByKey("admin.users.edit.title"));

    login.set(editingUser.getLogin());
    email.set(editingUser.getEmail());
    selectedRole.set(editingUser.getRole());

    if (editingUser.getImagePath() != null && !editingUser.getImagePath().isBlank()) {
      photoPreviewPath.set(editingUser.getImagePath());
      photoFileNameText.set(new File(editingUser.getImagePath()).getName());
    } else {
      photoPreviewPath.set(DEFAULT_IMAGE);
    }
  }

  public void sendCodeAsync() {
    if (sendingCode.get()) {
      return;
    }

    if (!prepareForCodeSending()) {
      return;
    }

    sendingCode.set(true);

    String targetEmail = pendingUserEmail();

    Task<Integer> task = new Task<>() {
      @Override
      protected Integer call() {
        return verificationService.sendVerificationCode(targetEmail);
      }
    };

    task.setOnSucceeded(event -> {
      sentCode = task.getValue();
      codeStep.set(true);
      sendingCode.set(false);
    });

    task.setOnFailed(event -> {
      emailError.set(LocalizationManager.getStringByKey("error.email.send_failed"));
      sendingCode.set(false);
    });

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }

  private boolean prepareForCodeSending() {
    clearErrors();

    try {
      User validatedUser = User.create(
          login.get(),
          password.get(),
          email.get(),
          selectedRole.get()
      );

      userService.validateLoginIsUnique(validatedUser.getLogin());

      pendingUser = validatedUser;
      return true;

    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;
    } catch (BusinessException e) {
      applyBusinessError(e);
      return false;
    }
  }

  public boolean save() {
    if (sendingCode.get()) {
      return false;
    }

    clearErrors();

    if (!saveSelectedPhotoIfNeeded()) {
      return false;
    }

    try {
      if (isEditMode()) {
        updateUser();
        return true;
      }

      return createUser();

    }
    catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;
    }
    catch (BusinessException e) {
      applyBusinessError(e);
      return false;
    }
  }

  private boolean createUser() {
    if (pendingUser == null) {
      codeError.set(LocalizationManager.getStringByKey("admin.users.send.code.first"));
      return false;
    }

    if (isBlank(code.get())) {
      codeError.set(LocalizationManager.getStringByKey("admin.users.code.required"));
      return false;
    }

    if (!String.valueOf(sentCode).equals(code.get().trim())) {
      codeError.set(LocalizationManager.getStringByKey("error.email.code_invalid"));
      return false;
    }

    pendingUser.setImagePath(imagePath);
    userService.add(pendingUser);
    return true;
  }

  private void updateUser() {
    String loginValue = isBlank(login.get()) ? editingUser.getLogin() : login.get().trim();
    String emailValue = isBlank(email.get()) ? editingUser.getEmail() : email.get().trim();
    Role roleValue = selectedRole.get() != null ? selectedRole.get() : editingUser.getRole();
    String passwordValue = isBlank(password.get()) ? null : password.get().trim();

    String finalImagePath = imagePath != null && !imagePath.isBlank()
        ? imagePath
        : editingUser.getImagePath();

    if (!loginValue.equals(editingUser.getLogin())) {
      userService.validateLoginIsUnique(loginValue);
    }

    userService.validateRoleChange(editingUser, roleValue);

    editingUser.updateProfile(loginValue, emailValue, roleValue, finalImagePath);

    if (passwordValue != null) {
      editingUser.changePassword(passwordValue);
    }

    userService.update(editingUser);
  }

  private boolean saveSelectedPhotoIfNeeded() {
    if (selectedPhotoFile == null) {
      return true;
    }

    try {
      imagePath = ImageStorageUtil.saveImage(selectedPhotoFile, "users");
      return true;
    } catch (Exception e) {
      photoError.set(LocalizationManager.getStringByKey("error.image.save.failed"));
      return false;
    }
  }

  public void selectPhoto(File file) {
    selectedPhotoFile = file;
    photoFileNameText.set(file.getName());
    photoPreviewPath.set(file.getAbsolutePath());
    photoError.set("");
  }

  public void goToFormStep() {
    if (!sendingCode.get()) {
      codeStep.set(false);
    }
  }

  private String pendingUserEmail() {
    if (pendingUser != null && pendingUser.getEmail() != null && !pendingUser.getEmail().isBlank()) {
      return pendingUser.getEmail();
    }

    return email.get() == null ? "" : email.get().trim();
  }

  private void applyValidationErrors(CustomEntityValidationExeption e) {
    e.getErrors().forEach((field, messages) -> {
      String text = messages.stream()
          .map(LocalizationManager::getStringByKey)
          .collect(Collectors.joining("\n"));

      switch (field) {
        case "login" -> loginError.set(text);
        case "password" -> passwordError.set(text);
        case "email" -> emailError.set(text);
        case "role" -> roleError.set(text);
        case "code" -> codeError.set(text);
        case "imagePath" -> photoError.set(text);
      }
    });
  }

  private void applyBusinessError(BusinessException e) {
    String key = e.getMessage();
    String message = LocalizationManager.getStringByKey(key);

    switch (key) {
      case "error.login.exists" -> loginError.set(message);
      case "error.user.edit.last_admin_role", "error.user.not_found" -> roleError.set(message);
      default -> roleError.set(message);
    }
  }

  private void clearErrors() {
    loginError.set("");
    passwordError.set("");
    emailError.set("");
    roleError.set("");
    codeError.set("");
    photoError.set("");
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  public boolean isEditMode() {
    return editingUser != null;
  }

  public StringProperty titleTextProperty() { return titleText; }
  public StringProperty saveButtonTextProperty() { return saveButtonText; }
  public StringProperty cancelButtonTextProperty() { return cancelButtonText; }
  public StringProperty uploadButtonTextProperty() { return uploadButtonText; }
  public StringProperty sendCodeButtonTextProperty() { return sendCodeButtonText; }
  public StringProperty backButtonTextProperty() { return backButtonText; }
  public StringProperty codeInfoTextProperty() { return codeInfoText; }

  public StringProperty loginLabelTextProperty() { return loginLabelText; }
  public StringProperty passwordLabelTextProperty() { return passwordLabelText; }
  public StringProperty emailLabelTextProperty() { return emailLabelText; }
  public StringProperty roleLabelTextProperty() { return roleLabelText; }
  public StringProperty codeLabelTextProperty() { return codeLabelText; }
  public StringProperty photoLabelTextProperty() { return photoLabelText; }
  public StringProperty photoFileNameTextProperty() { return photoFileNameText; }

  public StringProperty loginProperty() { return login; }
  public StringProperty passwordProperty() { return password; }
  public StringProperty emailProperty() { return email; }
  public StringProperty codeProperty() { return code; }

  public StringProperty loginErrorProperty() { return loginError; }
  public StringProperty passwordErrorProperty() { return passwordError; }
  public StringProperty emailErrorProperty() { return emailError; }
  public StringProperty roleErrorProperty() { return roleError; }
  public StringProperty codeErrorProperty() { return codeError; }
  public StringProperty photoErrorProperty() { return photoError; }

  public BooleanProperty codeStepProperty() { return codeStep; }
  public BooleanProperty sendingCodeProperty() { return sendingCode; }

  public ObjectProperty<Role> selectedRoleProperty() { return selectedRole; }
  public ObjectProperty<ObservableList<Role>> rolesProperty() { return roles; }

  public StringProperty photoPreviewPathProperty() { return photoPreviewPath; }
}