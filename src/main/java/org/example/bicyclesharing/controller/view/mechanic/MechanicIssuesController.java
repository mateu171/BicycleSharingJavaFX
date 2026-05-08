package org.example.bicyclesharing.controller.view.mechanic;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.mechanic.modalController.MechanicIssueDetailsController;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.DialogUtil;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.mechanic.MechanicIssuesViewModel;
import org.example.bicyclesharing.viewModel.mechanic.item.BikeIssueItemViewModel;

public class MechanicIssuesController extends BaseController {

  @FXML private Label searchLabel;
  @FXML private Label statusFilterLabel;
  @FXML private Label technicalFilterLabel;
  @FXML private Label sortLabel;
  @FXML private Label titleLabel;
  @FXML private Label countLabel;

  @FXML private TextField searchField;
  @FXML private ComboBox<String> statusFilterCombo;
  @FXML private ComboBox<String> technicalFilterCombo;
  @FXML private ComboBox<String> sortCombo;

  @FXML private Button takeInWorkButton;
  @FXML private Button resolveButton;
  @FXML private Button detailsButton;

  @FXML private TableView<BikeIssueItemViewModel> issuesTable;
  @FXML private TableColumn<BikeIssueItemViewModel, String> bikeColumn;
  @FXML private TableColumn<BikeIssueItemViewModel, String> technicalColumn;
  @FXML private TableColumn<BikeIssueItemViewModel, String> dateColumn;
  @FXML private TableColumn<BikeIssueItemViewModel, String> statusColumn;

  private MechanicIssuesViewModel viewModel;

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new MechanicIssuesViewModel(
        currentUser,
        AppConfig.bikeIssueService(),
        AppConfig.bicycleService(),
        AppConfig.rentalService()
    );

    setupComboBoxes();
    bind();
    setupTable();

    viewModel.initialize();
  }

  private void setupComboBoxes() {
    statusFilterCombo.getItems().setAll(
        LocalizationManager.getStringByKey("mechanic.filter.all"),
        LocalizationManager.getStringByKey("issue.status.new"),
        LocalizationManager.getStringByKey("issue.status.in_progress"),
        LocalizationManager.getStringByKey("issue.status.resolved")
    );

    technicalFilterCombo.getItems().setAll(
        LocalizationManager.getStringByKey("mechanic.filter.all"),
        LocalizationManager.getStringByKey("mechanic.filter.technical"),
        LocalizationManager.getStringByKey("mechanic.filter.nontechnical")
    );

    sortCombo.getItems().setAll(
        LocalizationManager.getStringByKey("mechanic.sort.newest"),
        LocalizationManager.getStringByKey("mechanic.sort.oldest"),
        LocalizationManager.getStringByKey("mechanic.sort.bike"),
        LocalizationManager.getStringByKey("mechanic.sort.status")
    );

    statusFilterCombo.getSelectionModel().selectFirst();
    technicalFilterCombo.getSelectionModel().selectFirst();
    sortCombo.getSelectionModel().selectFirst();
  }

  private void bind() {
    titleLabel.textProperty().bind(viewModel.titleTextProperty());
    searchLabel.textProperty().bind(viewModel.searchLabelTextProperty());
    statusFilterLabel.textProperty().bind(viewModel.statusFilterLabelTextProperty());
    technicalFilterLabel.textProperty().bind(viewModel.technicalFilterLabelTextProperty());
    sortLabel.textProperty().bind(viewModel.sortLabelTextProperty());

    detailsButton.textProperty().bind(viewModel.detailsButtonTextProperty());
    takeInWorkButton.textProperty().bind(viewModel.takeInWorkButtonTextProperty());
    resolveButton.textProperty().bind(viewModel.resolveButtonTextProperty());

    bikeColumn.textProperty().bind(viewModel.bikeColumnTextProperty());
    technicalColumn.textProperty().bind(viewModel.technicalColumnTextProperty());
    statusColumn.textProperty().bind(viewModel.statusColumnTextProperty());
    dateColumn.textProperty().bind(viewModel.dateColumnTextProperty());

    searchField.promptTextProperty().bind(viewModel.searchPromptTextProperty());
    countLabel.textProperty().bind(viewModel.countTextProperty());

    searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
    statusFilterCombo.valueProperty().bindBidirectional(viewModel.selectedStatusFilterProperty());
    technicalFilterCombo.valueProperty().bindBidirectional(viewModel.selectedTechnicalFilterProperty());
    sortCombo.valueProperty().bindBidirectional(viewModel.selectedSortProperty());

    issuesTable.getSelectionModel()
        .selectedItemProperty()
        .addListener((obs, oldValue, newValue) ->
            viewModel.selectedIssueProperty().set(newValue)
        );

    takeInWorkButton.disableProperty().bind(viewModel.takeInWorkDisabledProperty());
    resolveButton.disableProperty().bind(viewModel.resolveDisabledProperty());
    detailsButton.disableProperty().bind(
        issuesTable.getSelectionModel().selectedItemProperty().isNull()
    );
  }

  private void setupTable() {
    bikeColumn.setCellValueFactory(cell ->
        cell.getValue().bikeTextProperty());

    technicalColumn.setCellValueFactory(cell ->
        cell.getValue().technicalTextProperty());

    dateColumn.setCellValueFactory(cell ->
        cell.getValue().dateTextProperty());

    statusColumn.setCellValueFactory(cell ->
        cell.getValue().statusTextProperty());

    issuesTable.setItems(viewModel.getSortedIssues());
  }

  @FXML
  private void onTakeInWork() {
    viewModel.takeInWork();
    issuesTable.refresh();
  }

  @FXML
  private void onResolve() {
    viewModel.resolve();
    issuesTable.refresh();
  }

  @FXML
  private void onShowDetails() {
    BikeIssueItemViewModel selected = issuesTable.getSelectionModel().getSelectedItem();

    if (selected == null) {
      return;
    }

    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource(
              "/org/example/bicyclesharing/presentation/view/mechanic/modalView/MechanicIssueDetailsDialog.fxml"
          )
      );

      Parent root = loader.load();
      MechanicIssueDetailsController controller = loader.getController();

      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.initOwner(issuesTable.getScene().getWindow());
      dialogStage.initStyle(StageStyle.TRANSPARENT);

      Scene scene = new Scene(root);
      scene.getStylesheets().add(
          getClass().getResource(
              "/org/example/bicyclesharing/css/style.css"
          ).toExternalForm()
      );

      dialogStage.setScene(scene);
      controller.setStage(dialogStage);

      controller.setData(
          selected.bikeTextProperty().get(),
          selected.issue(),
          selected.dateTextProperty().get(),
          selected.statusTextProperty().get(),
          selected.technicalTextProperty().get()
      );

      dialogStage.showAndWait();

    } catch (IOException e) {
      DialogUtil.showError(
          LocalizationManager.getStringByKey("error.operation.failed")
      );
    }
  }
}