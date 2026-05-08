package org.example.bicyclesharing.controller.view.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.admin.modalController.AddEditStationController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.admin.StationManagementViewModel;
import org.example.bicyclesharing.viewModel.admin.item.StationItemViewModel;

public class StationManagementController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private Label countLabel;
  @FXML private TextField searchField;
  @FXML private ListView<StationItemViewModel> stationsListView;
  @FXML private Button addStationButton;

  private StationManagementViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new StationManagementViewModel(
        currentUser,
        AppConfig.stationService()
    );

    bind();
    setupList();
    viewModel.initialize();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    countLabel.textProperty().bind(viewModel.countTextProperty());
    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());
    addStationButton.textProperty().bind(viewModel.addStationButtonTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());

    searchField.textProperty().addListener((obs, oldVal, newVal) ->
        viewModel.applyFiltersAsync()
    );

    stationsListView.setItems(viewModel.getStations());
  }

  private void setupList() {
    stationsListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(StationItemViewModel item, boolean empty) {
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

  private VBox createCard(StationItemViewModel item) {
    VBox card = new VBox(8);
    card.getStyleClass().add("user-card");

    Label nameLabel = new Label();
    nameLabel.getStyleClass().add("user-card-title");
    nameLabel.textProperty().bind(item.nameTextProperty());

    Label coordsLabel = new Label();
    coordsLabel.getStyleClass().add("user-card-subtitle");
    coordsLabel.textProperty().bind(item.coordinatesTextProperty());

    Label infoLabel = new Label();
    infoLabel.getStyleClass().add("user-card-role");
    infoLabel.textProperty().bind(item.bicyclesCountTextProperty());

    Button editButton = new Button(LocalizationManager.getStringByKey("edit.button"));
    editButton.getStyleClass().add("button-edit");
    editButton.setOnAction(e -> openDialog(item));

    Button deleteButton = new Button(LocalizationManager.getStringByKey("admin.delete.button"));
    deleteButton.getStyleClass().add("button-danger");
    deleteButton.setOnAction(e -> deleteStation(item));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox actions = new HBox(10, editButton, deleteButton);
    HBox bottomRow = new HBox(10, spacer, actions);

    card.getChildren().addAll(nameLabel, coordsLabel, infoLabel, bottomRow);
    return card;
  }

  @FXML
  private void onAddStation() {
    openDialog(null);
  }

  private void openDialog(StationItemViewModel item) {
    try {
      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/admin/modalView/AddEditStationView.fxml",
          (AddEditStationController controller) ->
              controller.initData(
                  item == null ? null : item.getStation(),
                  viewModel::refreshAsync
              )
      );

    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.operation.failed"));
    }
  }

  private void deleteStation(StationItemViewModel item) {
    try {
      viewModel.delete(item);
    } catch (BusinessException e) {
      DialogUtil.showError(e.getMessage());
    } catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.delete.failed"));
    }
  }
}