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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.admin.modalController.AddEditBicycleController;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.exception.BusinessException;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.ImageStorageUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.admin.BicyclesManagementViewModel;

public class BicyclesManagementController extends BaseController {

  @FXML private Label titleLabel;
  @FXML private Label countLabel;
  @FXML private TextField searchField;
  @FXML private ComboBox<String> stateFilterComboBox;
  @FXML private Button addBikeButton;
  @FXML private ListView<Bicycle> bicyclesListView;

  private BicyclesManagementViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new BicyclesManagementViewModel(currentUser, AppConfig.bicycleService(),AppConfig.stationService());
    bindFields();
    setupFilters();
    setupList();
  }

  private void bindFields() {
    titleLabel.textProperty().bind(viewModel.titleText);
    countLabel.textProperty().bind(viewModel.countText);
    searchField.promptTextProperty().bind(viewModel.searchPromptText);
    addBikeButton.textProperty().bind(viewModel.addBikeButtonText);

    searchField.textProperty().bindBidirectional(viewModel.searchText);
    bicyclesListView.setItems(viewModel.getBicycles());
  }

  private void setupFilters() {
    stateFilterComboBox.setItems(FXCollections.observableArrayList(
        "ALL",
        LocalizationManager.getStringByKey(StateBicycle.AVAILABLE.getKey()),
        LocalizationManager.getStringByKey(StateBicycle.RENTED.getKey()),
        LocalizationManager.getStringByKey(StateBicycle.UNAVAILABLE.getKey()),
        LocalizationManager.getStringByKey(StateBicycle.NEEDS_INSPECTION.getKey()),
        LocalizationManager.getStringByKey(StateBicycle.ON_MAINTENANCE.getKey())
    ));
    stateFilterComboBox.getSelectionModel().selectFirst();

    stateFilterComboBox.setCellFactory(cb -> new ListCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setText(null);
        } else if (item.equals("ALL")) {
          setText(LocalizationManager.getStringByKey("all.text"));
        } else {
          setText(item);
        }
      }
    });

    stateFilterComboBox.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
          setText(null);
        } else if (item.equals("ALL")) {
          setText(LocalizationManager.getStringByKey("all.text"));
        } else {
          setText(item);
        }
      }
    });

    searchField.textProperty().addListener((obs, oldVal, newVal) -> viewModel.applyFilters());

    stateFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
      viewModel.selectedStateFilter.set(newVal);
      viewModel.applyFilters();
    });
  }

  private void setupList() {
    bicyclesListView.setCellFactory(list -> new ListCell<>() {
      @Override
      protected void updateItem(Bicycle bicycle, boolean empty) {
        super.updateItem(bicycle, empty);

        if (empty || bicycle == null) {
          setGraphic(null);
          setText(null);
          return;
        }

        VBox card = new VBox(8);
        card.getStyleClass().add("user-card");

        ImageView avatar = ImageStorageUtil.createImageView(bicycle.getImagePath(),60,60);
        avatar.getStyleClass().add("avatar");

        Label modelLabel = new Label(bicycle.getModel());
        modelLabel.getStyleClass().add("user-card-title");

        Label infoLabel = new Label(
            LocalizationManager.getStringByKey("admin.bicycles.price") + ": "
                + String.format("%.2f", bicycle.getPricePerMinute())
        );
        infoLabel.getStyleClass().add("user-card-subtitle");

        Label stateLabel = new Label(LocalizationManager.getStringByKey("admin.bicycles.state"));
        stateLabel.getStyleClass().add("user-card-role");

        Label stateLabelInfo = new Label(LocalizationManager.getStringByKey(bicycle.getState().getKey()));
        stateLabelInfo.getStyleClass().add("user-card.subtitle");

        Button editButton = new Button(LocalizationManager.getStringByKey("edit.button"));
        editButton.getStyleClass().add("button-edit");
        editButton.setOnAction(e -> openBikeDialog(bicycle));

        Button deleteButton = new Button(LocalizationManager.getStringByKey("admin.delete.button"));
        deleteButton.getStyleClass().add("button-danger");
        deleteButton.setOnAction(e -> {
          try {
            viewModel.deleteBicycle(bicycle);
          } catch (BusinessException ex) {
            DialogUtil.showError(ex.getMessage());
          } catch (Exception ex) {
            DialogUtil.showError(LocalizationManager.getStringByKey("error.delete.failed"));
          }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox stateBox = new HBox(8, stateLabel, stateLabelInfo);
        HBox actions = new HBox(10, editButton, deleteButton);
        HBox bottomRow = new HBox(10, stateBox, spacer, actions);

        card.getChildren().addAll(avatar,modelLabel, infoLabel, bottomRow);
        setGraphic(card);
      }
    });
  }

  @FXML
  private void onAddBike() {
   openBikeDialog(null);
  }


  private void openBikeDialog(Bicycle bicycle) {
    try {
      if (bicycle != null) {
        AppConfig.bicycleService().validateCanEdit(bicycle);
      }

      FXMLLoader loader = new FXMLLoader(
          getClass().getResource(
              "/org/example/bicyclesharing/presentation/view/admin/modalView/AddEditBicycleView.fxml")
      );

      Parent root = loader.load();

      AddEditBicycleController controller = loader.getController();
      controller.initData(bicycle, () -> {
        viewModel.loadBicycles();
        viewModel.applyFilters();
      });

      Scene scene = new Scene(root);
      scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
      scene.getStylesheets().add(
          getClass().getResource("/org/example/bicyclesharing/css/style.css").toExternalForm()
      );

      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initStyle(StageStyle.TRANSPARENT);
      stage.setScene(scene);
      stage.showAndWait();

    } catch (BusinessException e) {
      DialogUtil.showError(e.getMessage());
    } catch (Exception e) {
      DialogUtil.showError("error.operation.failed");
    }
  }
}