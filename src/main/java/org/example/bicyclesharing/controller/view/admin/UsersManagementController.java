package org.example.bicyclesharing.controller.view.admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.Role;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.admin.UsersManagementViewModel;

public class UsersManagementController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private Label countLabel;
  @FXML private TextField searchField;
  @FXML private ComboBox<String> roleFilterComboBox;
  @FXML private ListView<User> usersListView;

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

    searchField.textProperty().bindBidirectional(viewModel.searchText);
    usersListView.setItems(viewModel.getUsers());
  }

  private void setupFilters() {
    roleFilterComboBox.setItems(FXCollections.observableArrayList(
        LocalizationManager.getStringByKey("all.text"),
        Role.CLIENT.name(),
        Role.ADMIN.name()
    ));
    roleFilterComboBox.getSelectionModel().selectFirst();

    searchField.textProperty().addListener((obs, oldVal, newVal) -> {
      viewModel.applyFilters();
    });

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

        Label loginLabel = new Label(user.getLogin());
        loginLabel.getStyleClass().add("user-card-title");

        Label emailLabel = new Label(user.getEmail());
        emailLabel.getStyleClass().add("user-card-subtitle");

        Label roleLabel = new Label("Role:");
        roleLabel.getStyleClass().add("user-card-role");

        ComboBox<Role> roleComboBox = new ComboBox<>();
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        roleComboBox.setValue(user.getRole());
        roleComboBox.getStyleClass().add("settings-combo");

        roleComboBox.setCellFactory(cb -> new ListCell<>() {
          @Override
          protected void updateItem(Role item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
            } else {
              setText(item.getName());
            }
          }
        });

        roleComboBox.setButtonCell(new ListCell<>() {
          @Override
          protected void updateItem(Role item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText(null);
            } else {
              setText(item.getName());
            }
          }
        });

        roleComboBox.setOnAction(e -> {
          Role selectedRole = roleComboBox.getValue();
          if (selectedRole != null && selectedRole != user.getRole()) {
            viewModel.changeRole(user, selectedRole);
          }
        });

        Button deleteButton = new Button(LocalizationManager.getStringByKey("admin.delete.button"));
        deleteButton.getStyleClass().add("button-danger");
        deleteButton.setOnAction(e -> {
          viewModel.deleteUser(user);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox roleBox = new HBox(8, roleLabel, roleComboBox);
        HBox actions = new HBox(10, deleteButton);
        HBox bottomRow = new HBox(10, roleBox, spacer, actions);

        card.getChildren().addAll(loginLabel, emailLabel, bottomRow);
        setGraphic(card);
      }
    });
  }
}