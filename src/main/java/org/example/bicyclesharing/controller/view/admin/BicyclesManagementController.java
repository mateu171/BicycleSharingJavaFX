package org.example.bicyclesharing.controller.view.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.viewModel.admin.item.BicycleItemViewModel;
import org.example.bicyclesharing.controller.view.admin.modalController.AddEditBicycleController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.ImageStorageUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.util.WindowUtil;
import org.example.bicyclesharing.viewModel.admin.BicyclesManagementViewModel;

public class BicyclesManagementController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private Label countLabel;
  @FXML private TextField searchField;
  @FXML private ComboBox<String> stateFilterComboBox;
  @FXML private Button addBikeButton;
  @FXML private ListView<BicycleItemViewModel> bicyclesListView;

  private BicyclesManagementViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new BicyclesManagementViewModel(currentUser, AppConfig.bicycleService(),AppConfig.stationService());
    bindFields();
    setupFilters();
    setupList();
    viewModel.initialize();
  }

  private void bindFields() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    countLabel.textProperty().bind(viewModel.countTextProperty());
    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());
    addBikeButton.textProperty().bind(viewModel.addBikeButtonTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
    stateFilterComboBox.valueProperty().bindBidirectional(
        viewModel.selectedStateFilterProperty()
    );

    bicyclesListView.setItems(viewModel.getBicycles());

    searchField.textProperty().addListener((obs, oldVal, newVal) ->
        viewModel.applyFiltersAsync()
    );

    stateFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) ->
        viewModel.applyFiltersAsync()
    );
  }

  private void setupFilters() {
    stateFilterComboBox.setItems(viewModel.getStateFilters());
    stateFilterComboBox.getSelectionModel().selectFirst();

    stateFilterComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(toFilterText(item, empty));
      }
    });

    stateFilterComboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(toFilterText(item, empty));
      }
    });
  }

  private String toFilterText(String item,boolean empty)
  {
    if(empty || item == null)
    return null;

    if(item.equals("ALL"))
      return LocalizationManager.getStringByKey("all.text");

    return item;
  }

  private void setupList() {
    bicyclesListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(BicycleItemViewModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        VBox card = createCard(item);
        setGraphic(card);
      }
    });
  }

  private VBox createCard(BicycleItemViewModel item) {
    VBox card = new VBox(8);
    card.getStyleClass().add("user-card");

    ImageView avatar = ImageStorageUtil.createImageView(
        item.imagePathProperty().get(),
        60,
        60
    );
    avatar.getStyleClass().add("avatar");

    Label modelLabel = new Label();
    modelLabel.getStyleClass().add("user-card-title");
    modelLabel.textProperty().bind(item.modelTextProperty());

    Label priceLabel = new Label();
    priceLabel.getStyleClass().add("user-card-subtitle");
    priceLabel.textProperty().bind(item.priceTextProperty());

    Label stateLabel = new Label();
    stateLabel.getStyleClass().add("user-card-subtitle");
    stateLabel.textProperty().bind(item.stateTitleTextProperty());

    Label stateInfoLabel = new Label();
    stateInfoLabel.getStyleClass().add("user-card-subtitle");
    stateInfoLabel.textProperty().bind(item.stateTextProperty());

    Button editButton = new Button(LocalizationManager.getStringByKey("edit.button"));
    editButton.getStyleClass().add("button-edit");
    editButton.setOnAction(e -> openBikeDialog(item));

    Button deleteButton = new Button(LocalizationManager.getStringByKey("admin.delete.button"));
    deleteButton.getStyleClass().add("button-danger");
    deleteButton.setOnAction(e -> deleteBike(item));

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox stateBox = new HBox(8, stateLabel, stateInfoLabel);
    HBox actions = new HBox(10, editButton, deleteButton);
    HBox bottomRow = new HBox(10, stateBox, spacer, actions);

    card.getChildren().addAll(avatar, modelLabel, priceLabel, bottomRow);
    return card;
  }

  @FXML
  private void onAddBike() {
   openBikeDialog(null);
  }


  private void openBikeDialog(BicycleItemViewModel item) {
    try {
      if (item != null) {
       viewModel.validateCanEdit(item);
      }

      WindowUtil.openModal(
          "/org/example/bicyclesharing/presentation/view/admin/modalView/AddEditBicycleView.fxml",
          (AddEditBicycleController controller) -> controller.initData(item == null ? null : item.getBicycle(), viewModel::refreshAsync)
      );

    } catch (BusinessException e) {
      DialogUtil.showError(e.getMessage());
    } catch (Exception e) {
      DialogUtil.showError("error.operation.failed");
    }
  }

  private void deleteBike(BicycleItemViewModel item)
  {
    try {
      viewModel.deleteBicycle(item);
    }catch (BusinessException e)
    {
      DialogUtil.showError(e.getMessage());
    }catch (Exception e) {
      DialogUtil.showError(LocalizationManager.getStringByKey("error.delete.failed"));
    }
  }
}