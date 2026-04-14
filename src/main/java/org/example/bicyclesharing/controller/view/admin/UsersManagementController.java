package org.example.bicyclesharing.controller.view.admin;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.admin.modalController.AddEditUserController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.ImageStorageUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.admin.UsersManagementViewModel;

public class UsersManagementController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private Label countLabel;
  @FXML private TextField searchField;
  @FXML private ComboBox<String> roleFilterComboBox;
  @FXML private ListView<User> usersListView;
  @FXML private Button addUserButton;

  private UsersManagementViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new UsersManagementViewModel(currentUser, AppConfig.userService());
    bindFields();
    setupFilters();
    setupList();
  }

  private void bindFields() {
    titleLabel.textProperty().bind(viewModel.titleText);
    countLabel.textProperty().bind(viewModel.countText);
    searchField.promptTextProperty().bind(viewModel.searchPromptText);
    addUserButton.textProperty().bind(viewModel.addButtonText);

    searchField.textProperty().bindBidirectional(viewModel.searchText);
    usersListView.setItems(viewModel.getUsers());
  }

  private void setupFilters() {
    roleFilterComboBox.setItems(FXCollections.observableArrayList(
        LocalizationManager.getStringByKey("all.text"),
        LocalizationManager.getStringByKey(Role.ADMIN.getKey()),
        LocalizationManager.getStringByKey(Role.MANAGER.getKey()),
        LocalizationManager.getStringByKey(Role.MECHANIC.getKey())
    ));
    roleFilterComboBox.getSelectionModel().selectFirst();

    searchField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.applyFilters());

    roleFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
      viewModel.selectedRoleFilter.set(newVal);
      viewModel.applyFilters();
    });
  }

  private void setupList() {
    usersListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        VBox card = new VBox(8);
        card.getStyleClass().add("user-card");

        ImageView avatar = ImageStorageUtil.createImageView(user.getImagePath(),60,60);
        avatar.getStyleClass().add("avatar");

        Label loginLabel = new Label(user.getLogin());
        loginLabel.getStyleClass().add("user-card-title");

        Label emailLabel = new Label(user.getEmail());
        emailLabel.getStyleClass().add("user-card-subtitle");

        Label roleLabelText = new Label(LocalizationManager.getStringByKey("admin.users.role"));
        roleLabelText.getStyleClass().add("user-card-subtitle");

        Label roleLabel = new Label(LocalizationManager.getStringByKey(user.getRole().getKey()));
        roleLabel.getStyleClass().add("user-card-subtitle");

        Button editButton = new Button(LocalizationManager.getStringByKey("edit.button"));
        editButton.getStyleClass().add("button-edit");
        editButton.setOnAction(e -> openUserDialog(user));

        Button deleteButton = new Button(LocalizationManager.getStringByKey("admin.delete.button"));
        deleteButton.getStyleClass().add("button-danger");
        deleteButton.setOnAction(e ->
        {
          try {
            viewModel.deleteUser(user);
          }catch (BusinessException ex)
          {
            DialogUtil.showError(ex.getMessage());
          }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox infoBox = new VBox(5,loginLabel,emailLabel);

        HBox roleBox = new HBox(8, roleLabelText, roleLabel);
        HBox actions = new HBox(10, editButton, deleteButton);
        HBox bottomRow = new HBox(10, roleBox, spacer, actions);

        card.getChildren().addAll(avatar,infoBox, bottomRow);
        setGraphic(card);
      }
    });
  }

  @FXML
  private void onAddUser() {
    openUserDialog(null);
  }

  private void openUserDialog(User user) {
    try {
      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/admin/modalView/AddEditUserView.fxml",
          (AddEditUserController controller) -> controller.initData(user, () -> {
            viewModel.loadUsers();
            viewModel.applyFilters();
          })
      );

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}