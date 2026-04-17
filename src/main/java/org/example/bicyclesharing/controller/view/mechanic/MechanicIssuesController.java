package org.example.bicyclesharing.controller.view.mechanic;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.bicyclesharing.controller.view.BaseController;
import org.example.bicyclesharing.controller.view.mechanic.modalController.MechanicIssueDetailsController;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.mechanic.MechanicIssuesViewModel;
import org.example.bicyclesharing.viewModel.mechanic.MechanicServiceViewModel;

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

  @FXML private TableView<BikeIssue> issuesTable;
  @FXML private TableColumn<BikeIssue, String> bikeColumn;
  @FXML private TableColumn<BikeIssue, String> technicalColumn;
  @FXML private TableColumn<BikeIssue, String> dateColumn;
  @FXML private TableColumn<BikeIssue, String> statusColumn;

  private MechanicIssuesViewModel viewModel;

  @FXML
  public void initialize() {
    statusFilterCombo.getItems().addAll(
        LocalizationManager.getStringByKey("mechanic.filter.all"),
        LocalizationManager.getStringByKey("issue.status.new"),
        LocalizationManager.getStringByKey("issue.status.in_progress"),
        LocalizationManager.getStringByKey("issue.status.resolved")
    );
    statusFilterCombo.getSelectionModel().selectFirst();

    technicalFilterCombo.getItems().addAll(
        LocalizationManager.getStringByKey("mechanic.filter.all"),
        LocalizationManager.getStringByKey("mechanic.filter.technical"),
        LocalizationManager.getStringByKey("mechanic.filter.nontechnical")
    );
    technicalFilterCombo.getSelectionModel().selectFirst();

    sortCombo.getItems().addAll(
        LocalizationManager.getStringByKey("mechanic.sort.newest"),
        LocalizationManager.getStringByKey("mechanic.sort.oldest"),
        LocalizationManager.getStringByKey("mechanic.sort.bike"),
        LocalizationManager.getStringByKey("mechanic.sort.status")
    );
    sortCombo.getSelectionModel().selectFirst();
  }

  @FXML
  private void onTakeInWork()
  {
    BikeIssue selected = issuesTable.getSelectionModel().getSelectedItem();
    if(selected == null)
      return;

    viewModel.takeInWork(selected);
    issuesTable.refresh();
  }

  @FXML
  private void onResolve()
  {
    BikeIssue selected = issuesTable.getSelectionModel().getSelectedItem();
    if(selected == null)
      return;

    viewModel.resolve(selected);
    issuesTable.refresh();
  }

  @FXML
  private void onShowDetails()
  {
    BikeIssue selected = issuesTable.getSelectionModel().getSelectedItem();
    if(selected == null)
      return;

    try {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource("/org/example/bicyclesharing/presentation/view/mechanic/modalView/MechanicIssueDetailsDialog.fxml")
      );
      Parent root = loader.load();
      MechanicIssueDetailsController controller = loader.getController();

      Stage dialogStage = new Stage();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.initOwner(issuesTable.getScene().getWindow());
      dialogStage.initStyle(StageStyle.TRANSPARENT);

      Scene scene = new Scene(root);

      scene.getStylesheets().add(getClass().getResource("/org/example/bicyclesharing/css/style.css").toExternalForm());

      dialogStage.setScene(scene);
      controller.setStage(dialogStage);
      controller.setData(viewModel.getBikeModel(selected),selected,viewModel.getDate(selected),viewModel.getStatus(selected),viewModel.getTechnical(selected));

      dialogStage.showAndWait();
    }catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void setCurrentUser(User currentUser) {
    viewModel = new MechanicIssuesViewModel(currentUser, AppConfig.bikeIssueService(),AppConfig.bicycleService(),AppConfig.rentalService());
    bind();
    viewModel.loadIssuesAsync();
    setupTable();
    viewModel.updateCount();
  }

  private void bind()
  {
    titleLabel.textProperty().bind(viewModel.titleText);
    searchLabel.textProperty().bind(viewModel.searchLabelText);
    statusFilterLabel.textProperty().bind(viewModel.statusFilterLabelText);
    technicalFilterLabel.textProperty().bind(viewModel.technicalFilterLabelText);
    sortLabel.textProperty().bind(viewModel.sortLabelText);
    detailsButton.textProperty().bind(viewModel.detailsButtonText);
    takeInWorkButton.textProperty().bind(viewModel.takeInWorkButtonText);
    resolveButton.textProperty().bind(viewModel.resolveButtonText);
    bikeColumn.textProperty().bind(viewModel.bikeColumnText);
    technicalColumn.textProperty().bind(viewModel.technicalColumnText);
    statusColumn.textProperty().bind(viewModel.statusColumnText);
    dateColumn.textProperty().bind(viewModel.dateColumnText);

    searchField.promptTextProperty().bind(viewModel.searchPromptText);
    countLabel.textProperty().bind(viewModel.countText);

    takeInWorkButton.disableProperty().bind(
        Bindings.createBooleanBinding(
            () -> !viewModel.canTakeInWork(issuesTable.getSelectionModel().getSelectedItem()),
            issuesTable.getSelectionModel().selectedItemProperty()
        )
    );

    resolveButton.disableProperty().bind(
        Bindings.createBooleanBinding(
            () -> !viewModel.canResolve(issuesTable.getSelectionModel().getSelectedItem()),
            issuesTable.getSelectionModel().selectedItemProperty()
        )
    );
  }

  private void setupTable() {

    bikeColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(viewModel.getBikeModel(cell.getValue())));
    technicalColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(viewModel.getTechnical(cell.getValue())));
    dateColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(viewModel.getDate(cell.getValue())));
    statusColumn.setCellValueFactory(cell ->
        new SimpleStringProperty(viewModel.getStatus(cell.getValue())));

    FilteredList<BikeIssue> filtered =
        new FilteredList<>(viewModel.getIssues(), issue -> true);

    Runnable refilter = () -> filtered.setPredicate(issue ->
        viewModel.matchesSearch(issue, searchField.getText())
            && viewModel.matchesStatus(issue, statusFilterCombo.getValue())
            && viewModel.matchesTechnical(issue, technicalFilterCombo.getValue())
    );

    searchField.textProperty().addListener((obs, oldV, newV) -> refilter.run());
    statusFilterCombo.valueProperty().addListener((obs, oldV, newV) -> refilter.run());
    technicalFilterCombo.valueProperty().addListener((obs, oldV, newV) -> refilter.run());

    SortedList<BikeIssue> sorted = new SortedList<>(filtered);

    sortCombo.valueProperty().addListener((obs, oldV, newV) ->
        sorted.setComparator(viewModel.getComparator(newV)));

    sorted.setComparator(viewModel.getComparator(sortCombo.getValue()));

    issuesTable.setItems(sorted);

    detailsButton.disableProperty().bind(
        Bindings.isNull(issuesTable.getSelectionModel().selectedItemProperty())
    );
  }
}