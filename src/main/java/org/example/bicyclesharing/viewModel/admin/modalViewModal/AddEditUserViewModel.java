package org.example.bicyclesharing.viewModel.admin.modalViewModal;

import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;
import org.example.bicyclesharing.services.UserService;
import org.example.bicyclesharing.services.VerificationService;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.LocalizationManager;

public class AddEditUserViewModel {

  private final UserService userService;
  private final VerificationService verificationService;
  private final User editingUser;

  public final StringProperty titleText = new SimpleStringProperty();

  public final StringProperty saveButtonText = LocalizationManager.getStringProperty("save.button");
  public final StringProperty cancelButtonText = LocalizationManager.getStringProperty("cancel.button");
  public final StringProperty uploadButtonText = LocalizationManager.getStringProperty("uploadPhoto.button.text");
  public final StringProperty sendCodeButtonText = LocalizationManager.getStringProperty("button.send.code");
  public final StringProperty backButtonText = LocalizationManager.getStringProperty("button.back");
  public final StringProperty codeInfoText = LocalizationManager.getStringProperty("verification.code.info");

  public final StringProperty loginLabelText = LocalizationManager.getStringProperty("register.login");
  public final StringProperty passwordLabelText = LocalizationManager.getStringProperty("register.password");
  public final StringProperty emailLabelText = LocalizationManager.getStringProperty("register.email");
  public final StringProperty roleLabelText = LocalizationManager.getStringProperty("admin.users.role");
  public final StringProperty codeLabelText = LocalizationManager.getStringProperty("register.code");
  public final StringProperty photoLabelText = LocalizationManager.getStringProperty("admin.users.photo");
  public final StringProperty photoFileNameText = LocalizationManager.getStringProperty("file.not.selected");

  public final StringProperty login = new SimpleStringProperty("");
  public final StringProperty password = new SimpleStringProperty("");
  public final StringProperty email = new SimpleStringProperty("");
  public final StringProperty code = new SimpleStringProperty("");

  public final StringProperty loginError = new SimpleStringProperty("");
  public final StringProperty passwordError = new SimpleStringProperty("");
  public final StringProperty emailError = new SimpleStringProperty("");
  public final StringProperty roleError = new SimpleStringProperty("");
  public final StringProperty codeError = new SimpleStringProperty("");
  public final StringProperty photoError = new SimpleStringProperty("");

  public final BooleanProperty codeStep = new SimpleBooleanProperty(false);

  private Role selectedRole;
  private int sentCode;
  private User pendingUser;
  private String imagePath;

  public AddEditUserViewModel(
      UserService userService,
      VerificationService verificationService,
      User editingUser
  ) {
    this.userService = userService;
    this.verificationService = verificationService;
    this.editingUser = editingUser;

    if (editingUser == null) {
      titleText.set(LocalizationManager.getStringByKey("admin.users.add.title"));
    } else {
      titleText.set(LocalizationManager.getStringByKey("admin.users.edit.title"));
      selectedRole = editingUser.getRole();

      login.set(editingUser.getLogin());
      email.set(editingUser.getEmail());
    }
  }

  public boolean isEditMode() {
    return editingUser != null;
  }

  public void setSelectedRole(Role selectedRole) {
    this.selectedRole = selectedRole;
    roleError.set("");
  }

  public void goToCodeStep() {
    codeStep.set(true);
  }

  public void goToFormStep() {
    codeStep.set(false);
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public void setPhotoError(String message) {
    photoError.set(message);
  }

  public boolean sendCode() {
    clearErrors();

    try {
      User validatedUser = User.create(
          login.get(),
          password.get(),
          email.get(),
          selectedRole
      );

      if (userService.existsByLogin(validatedUser.getLogin())) {
        loginError.set(LocalizationManager.getStringByKey("error.login.exists"));
        return false;
      }

      int code = verificationService.sendVerificationCode(validatedUser.getEmail());

      sentCode = code;
      pendingUser = validatedUser;
      return true;

    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;
    } catch (Exception e) {
      emailError.set(LocalizationManager.getStringByKey("error.email.send_failed"));
      return false;
    }
  }

  public boolean save() {
    clearErrors();

    try {
      if (editingUser == null) {
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

        if (!pendingUser.isValid()) {
          throw new CustomEntityValidationExeption(pendingUser.getErrors());
        }

        userService.add(pendingUser);

      } else {
        String loginValue = isBlank(login.get()) ? editingUser.getLogin() : login.get().trim();
        String emailValue = isBlank(email.get()) ? editingUser.getEmail() : email.get().trim();
        Role roleValue = selectedRole != null ? selectedRole : editingUser.getRole();
        String passwordValue = isBlank(password.get()) ? null : password.get().trim();
        String finalImagePath = (imagePath != null && !imagePath.isBlank())
            ? imagePath
            : editingUser.getImagePath();

        if (!loginValue.equals(editingUser.getLogin()) && userService.existsByLogin(loginValue)) {
          loginError.set(LocalizationManager.getStringByKey("error.login.exists"));
          return false;
        }

        userService.validateRoleChange(editingUser,roleValue);

        editingUser.updateProfile(loginValue, emailValue, roleValue, finalImagePath);

        if (passwordValue != null) {
          editingUser.changePassword(passwordValue);
        }

        userService.update(editingUser);
      }

      return true;

    } catch (CustomEntityValidationExeption e) {
      applyValidationErrors(e);
      return false;
    }  catch (BusinessException e) {
      roleError.set(LocalizationManager.getStringByKey(e.getMessage()));
      return false;
    }
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

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void clearErrors() {
    loginError.set("");
    passwordError.set("");
    emailError.set("");
    roleError.set("");
    codeError.set("");
    photoError.set("");
  }
}