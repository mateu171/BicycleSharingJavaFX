package org.example.bicyclesharing.controller.view.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.admin.modalController.AddEditUserController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.ImageStorageUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.admin.UsersManagementViewModel;
import org.example.bicyclesharing.viewModel.admin.item.UserItemViewModel;

public class UsersManagementController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private Label countLabel;
  @FXML private TextField searchField;
  @FXML private ComboBox<String> roleFilterComboBox;
  @FXML private ListView<UserItemViewModel> usersListView;
  @FXML private Button addUserButton;

  private UsersManagementViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new UsersManagementViewModel(
        currentUser,
        AppConfig.userService()
    );

    bind();
    setupFilters();
    setupList();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    countLabel.textProperty().bind(viewModel.countTextProperty());
    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());
    addUserButton.textProperty().bind(viewModel.addButtonTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
    roleFilterComboBox.valueProperty().bindBidirectional(viewModel.selectedRoleFilterProperty());

    usersListView.setItems(viewModel.getUsers());

    searchField.textProperty().addListener((obs, oldVal, newVal) ->
        viewModel.applyFiltersAsync()
    );

    roleFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) ->
        viewModel.applyFiltersAsync()
    );
  }

  private void setupFilters() {
    roleFilterComboBox.setItems(viewModel.getRoleFilters());
    roleFilterComboBox.getSelectionModel().selectFirst();
  }

  private void setupList() {
    usersListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(UserItemViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        setGraphic(createCard(item));
      }
    });
  }

  private VBox createCard(UserItemViewModel item) {
    VBox card = new VBox(8);
    card.getStyleClass().add("user-card");

    ImageView avatar = ImageStorageUtil.createImageView(
        item.imagePathProperty().get(),
        60,
        60
    );
    avatar.getStyleClass().add("avatar");

    Label loginLabel = new Label();
    loginLabel.getStyleClass().add("user-card-title");
    loginLabel.textProperty().bind(item.loginTextProperty());

    Label emailLabel = new Label();
    emailLabel.getStyleClass().add("user-card-subtitle");
    emailLabel.textProperty().bind(item.emailTextProperty());

    Label roleTitleLabel = new Label();
    roleTitleLabel.getStyleClass().add("user-card-subtitle");
    roleTitleLabel.textProperty().bind(item.roleTitleTextProperty());

    Label roleLabel = new Label();
    roleLabel.getStyleClass().add("user-card-subtitle");
    roleLabel.textProperty().bind(item.roleTextProperty());

    Button editButton = new Button(LocalizationManager.getStringByKey("edit.button"));
    editButton.getStyleClass().add("button-edit");
    editButton.setOnAction(e -> openUserDialog(item));

    Button deleteButton = new Button(LocalizationManager.getStringByKey("admin.delete.button"));
    deleteButton.getStyleClass().add("button-danger");
    deleteButton.setOnAction(e -> deleteUser(item));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    VBox infoBox = new VBox(5, loginLabel, emailLabel);
    HBox roleBox = new HBox(8, roleTitleLabel, roleLabel);
    HBox actions = new HBox(10, editButton, deleteButton);
    HBox bottomRow = new HBox(10, roleBox, spacer, actions);

    card.getChildren().addAll(avatar, infoBox, bottomRow);
    return card;
  }

  @FXML
  private void onAddUser() {
    openUserDialog(null);
  }

  private void openUserDialog(UserItemViewModel item) {
    try {
      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/admin/modalView/AddEditUserView.fxml",
          (AddEditUserController controller) ->
              controller.initData(
                  item == null ? null : item.getUser(),
                  viewModel::refreshAsync
              )
      );
    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.operation.failed"));
    }
  }

  private void deleteUser(UserItemViewModel item) {
    try {
      viewModel.deleteUser(item);
    } catch (BusinessException e) {
      DialogUtil.showError(e.getMessage());
    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.delete.failed"));
    }
  }
}