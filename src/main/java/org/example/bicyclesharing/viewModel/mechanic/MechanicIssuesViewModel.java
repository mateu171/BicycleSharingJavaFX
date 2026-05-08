package org.example.bicyclesharing.viewModel.mechanic;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.BikeIssueService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.AsyncViewModel;
import org.example.bicyclesharing.viewModel.mechanic.item.BikeIssueItemViewModel;

public class MechanicIssuesViewModel extends AsyncViewModel {

  private final BikeIssueService bikeIssueService;
  private final BicycleService bicycleService;
  private final RentalService rentalService;

  private final ObservableList<BikeIssueItemViewModel> issues =
      FXCollections.observableArrayList();

  private final FilteredList<BikeIssueItemViewModel> filteredIssues =
      new FilteredList<>(issues);

  private final SortedList<BikeIssueItemViewModel> sortedIssues =
      new SortedList<>(filteredIssues);

  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("mechanic.issues.title");

  private final StringProperty bikeColumnText =
      LocalizationManager.getStringProperty("mechanic.column.bike");

  private final StringProperty technicalColumnText =
      LocalizationManager.getStringProperty("mechanic.column.technical");

  private final StringProperty dateColumnText =
      LocalizationManager.getStringProperty("mechanic.column.date");

  private final StringProperty statusColumnText =
      LocalizationManager.getStringProperty("mechanic.column.status");

  private final StringProperty searchLabelText =
      LocalizationManager.getStringProperty("mechanic.search");

  private final StringProperty searchPromptText =
      LocalizationManager.getStringProperty("mechanic.search.prompt");

  private final StringProperty statusFilterLabelText =
      LocalizationManager.getStringProperty("mechanic.issues.filter.status");

  private final StringProperty technicalFilterLabelText =
      LocalizationManager.getStringProperty("mechanic.issues.filter.technical");

  private final StringProperty sortLabelText =
      LocalizationManager.getStringProperty("mechanic.sort");

  private final StringProperty detailsButtonText =
      LocalizationManager.getStringProperty("mechanic.button.details");

  private final StringProperty resolveButtonText =
      LocalizationManager.getStringProperty("mechanic.button.resolve");

  private final StringProperty takeInWorkButtonText =
      LocalizationManager.getStringProperty("mechanic.button.take");

  private final StringProperty countText =
      new SimpleStringProperty("");

  private final StringProperty searchText =
      new SimpleStringProperty("");

  private final StringProperty selectedStatusFilter =
      new SimpleStringProperty();

  private final StringProperty selectedTechnicalFilter =
      new SimpleStringProperty();

  private final StringProperty selectedSort =
      new SimpleStringProperty();

  private final ObjectProperty<BikeIssueItemViewModel> selectedIssue =
      new SimpleObjectProperty<>();

  private final BooleanProperty takeInWorkDisabled =
      new SimpleBooleanProperty(true);

  private final BooleanProperty resolveDisabled =
      new SimpleBooleanProperty(true);

  public MechanicIssuesViewModel(
      User currentUser,
      BikeIssueService bikeIssueService,
      BicycleService bicycleService,
      RentalService rentalService
  ) {
    super(currentUser);

    this.bikeIssueService = bikeIssueService;
    this.bicycleService = bicycleService;
    this.rentalService = rentalService;

    setupFiltering();
    setupSorting();
    setupSelection();
  }

  public void initialize() {
    loadIssuesAsync();
  }

  private void setupFiltering() {

    Runnable filterAction = () -> filteredIssues.setPredicate(this::matchesFilters);

    searchText.addListener((obs, oldValue, newValue) -> filterAction.run());

    selectedStatusFilter.addListener(
        (obs, oldValue, newValue) -> filterAction.run());

    selectedTechnicalFilter.addListener(
        (obs, oldValue, newValue) -> filterAction.run());
  }

  private boolean matchesFilters(BikeIssueItemViewModel item) {

    String search = searchText.get() == null
        ? ""
        : searchText.get().toLowerCase(Locale.ROOT).trim();

    boolean matchesSearch =
        search.isBlank()
            || safe(item.bikeTextProperty().get()).contains(search)
            || safe(item.problemTextProperty().get()).contains(search)
            || safe(item.commentTextProperty().get()).contains(search)
            || safe(item.statusTextProperty().get()).contains(search);

    String allFilter =
        LocalizationManager.getStringByKey("mechanic.filter.all");

    boolean matchesStatus =
        selectedStatusFilter.get() == null
            || selectedStatusFilter.get().equals(allFilter)
            || item.statusTextProperty().get().equals(selectedStatusFilter.get());

    boolean matchesTechnical =
        selectedTechnicalFilter.get() == null
            || selectedTechnicalFilter.get().equals(allFilter)
            || (
            selectedTechnicalFilter.get().equals(
                LocalizationManager.getStringByKey("mechanic.filter.technical"))
                && item.issue().isTechnicalProblem()
        )
            || (
            selectedTechnicalFilter.get().equals(
                LocalizationManager.getStringByKey("mechanic.filter.nontechnical"))
                && !item.issue().isTechnicalProblem()
        );

    return matchesSearch && matchesStatus && matchesTechnical;
  }

  private void setupSorting() {

    selectedSort.addListener((obs, oldValue, newValue) ->
        sortedIssues.setComparator(resolveComparator(newValue)));

    sortedIssues.setComparator(resolveComparator(selectedSort.get()));
  }

  private Comparator<BikeIssueItemViewModel> resolveComparator(String sortValue) {

    String newest =
        LocalizationManager.getStringByKey("mechanic.sort.newest");

    String oldest =
        LocalizationManager.getStringByKey("mechanic.sort.oldest");

    String bike =
        LocalizationManager.getStringByKey("mechanic.sort.bike");

    String status =
        LocalizationManager.getStringByKey("mechanic.sort.status");

    if (oldest.equals(sortValue)) {
      return Comparator.comparing(
          item -> item.issue().getCreatedAt(),
          Comparator.nullsLast(Comparator.naturalOrder())
      );
    }

    if (bike.equals(sortValue)) {
      return Comparator.comparing(
          item -> item.bikeTextProperty().get(),
          String.CASE_INSENSITIVE_ORDER
      );
    }

    if (status.equals(sortValue)) {
      return Comparator.comparing(
          item -> item.statusTextProperty().get(),
          String.CASE_INSENSITIVE_ORDER
      );
    }

    return Comparator.comparing(
        (BikeIssueItemViewModel item) -> item.issue().getCreatedAt(),
        Comparator.nullsLast(Comparator.reverseOrder())
    );
  }

  private void setupSelection() {

    selectedIssue.addListener((obs, oldValue, newValue) -> {

      takeInWorkDisabled.set(
          newValue == null || !canTakeInWork(newValue.issue())
      );

      resolveDisabled.set(
          newValue == null || !canResolve(newValue.issue())
      );
    });
  }

  public void loadIssuesAsync() {

    runAsync(
        bikeIssueService::getAll,
        this::setIssues
    );
  }

  private void setIssues(List<BikeIssue> result) {

    issues.setAll(
        result.stream()
            .map(this::toItemViewModel)
            .toList()
    );

    updateCount();
  }

  private BikeIssueItemViewModel toItemViewModel(BikeIssue issue) {

    return new BikeIssueItemViewModel(
        issue,
        getBikeModel(issue),
        getProblem(issue),
        getComment(issue),
        getTechnical(issue),
        getDate(issue),
        getStatus(issue)
    );
  }

  public void updateCount() {

    countText.set(
        LocalizationManager.getStringByKey("mechanic.count")
            + ": "
            + filteredIssues.size()
    );
  }

  public String getBikeModel(BikeIssue issue) {

    Rental rental = rentalService.getById(issue.getRentalId());

    Bicycle bicycle =
        bicycleService.getById(rental.getBicycleId()).orElse(null);

    return bicycle != null
        ? bicycle.getModel()
        : "-";
  }

  public String getProblem(BikeIssue issue) {
    return issue.getProblemType();
  }

  public String getComment(BikeIssue issue) {

    return issue.getComment() == null
        || issue.getComment().isBlank()
        ? "-"
        : issue.getComment();
  }

  public String getTechnical(BikeIssue issue) {

    return issue.isTechnicalProblem()
        ? LocalizationManager.getStringByKey("common.yes")
        : LocalizationManager.getStringByKey("common.no");
  }

  public String getDate(BikeIssue issue) {

    return issue.getCreatedAt() == null
        ? "-"
        : issue.getCreatedAt().format(formatter);
  }

  public String getStatus(BikeIssue issue) {

    return LocalizationManager.getStringByKey(
        issue.getStatus().getKey()
    );
  }

  public boolean canTakeInWork(BikeIssue issue) {

    return issue != null
        && issue.getStatus() == IssueStatus.NEW;
  }

  public boolean canResolve(BikeIssue issue) {

    return issue != null
        && (
        issue.getStatus() == IssueStatus.NEW
            || issue.getStatus() == IssueStatus.IN_PROGRESS
    );
  }

  public void takeInWork() {

    if (selectedIssue.get() == null) {
      return;
    }

    BikeIssue issue = selectedIssue.get().issue();

    if (!canTakeInWork(issue)) {
      return;
    }

    issue.setStatus(IssueStatus.IN_PROGRESS);
    bikeIssueService.update(issue);

    if (issue.isTechnicalProblem()) {

      Rental rental =
          rentalService.getById(issue.getRentalId());

      Bicycle bicycle =
          bicycleService.getById(rental.getBicycleId()).orElse(null);

      if (bicycle != null) {

        bicycle.setState(StateBicycle.ON_MAINTENANCE);
        bicycleService.update(bicycle);
      }
    }

    loadIssuesAsync();
  }

  public void resolve() {

    if (selectedIssue.get() == null) {
      return;
    }

    BikeIssue issue = selectedIssue.get().issue();

    if (!canResolve(issue)) {
      return;
    }

    issue.setStatus(IssueStatus.RESOLVED);
    bikeIssueService.update(issue);

    Rental rental =
        rentalService.getById(issue.getRentalId());

    if (issue.isTechnicalProblem()) {

      Bicycle bicycle =
          bicycleService.getById(rental.getBicycleId()).orElse(null);

      if (bicycle != null
          && (
          bicycle.getState() == StateBicycle.NEEDS_INSPECTION
              || bicycle.getState() == StateBicycle.ON_MAINTENANCE
      )) {

        bicycle.setState(StateBicycle.AVAILABLE);
        bicycleService.update(bicycle);
      }
    }

    loadIssuesAsync();
  }

  private String safe(String value) {
    return value == null
        ? ""
        : value.toLowerCase(Locale.ROOT);
  }

  public SortedList<BikeIssueItemViewModel> getSortedIssues() {
    return sortedIssues;
  }

  public ObjectProperty<BikeIssueItemViewModel> selectedIssueProperty() {
    return selectedIssue;
  }

  public StringProperty searchTextProperty() {
    return searchText;
  }

  public StringProperty selectedStatusFilterProperty() {
    return selectedStatusFilter;
  }

  public StringProperty selectedTechnicalFilterProperty() {
    return selectedTechnicalFilter;
  }

  public StringProperty selectedSortProperty() {
    return selectedSort;
  }

  public BooleanProperty takeInWorkDisabledProperty() {
    return takeInWorkDisabled;
  }

  public BooleanProperty resolveDisabledProperty() {
    return resolveDisabled;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty bikeColumnTextProperty() {
    return bikeColumnText;
  }

  public StringProperty technicalColumnTextProperty() {
    return technicalColumnText;
  }

  public StringProperty dateColumnTextProperty() {
    return dateColumnText;
  }

  public StringProperty statusColumnTextProperty() {
    return statusColumnText;
  }

  public StringProperty searchLabelTextProperty() {
    return searchLabelText;
  }

  public StringProperty searchPromptTextProperty() {
    return searchPromptText;
  }

  public StringProperty statusFilterLabelTextProperty() {
    return statusFilterLabelText;
  }

  public StringProperty technicalFilterLabelTextProperty() {
    return technicalFilterLabelText;
  }

  public StringProperty sortLabelTextProperty() {
    return sortLabelText;
  }

  public StringProperty detailsButtonTextProperty() {
    return detailsButtonText;
  }

  public StringProperty resolveButtonTextProperty() {
    return resolveButtonText;
  }

  public StringProperty takeInWorkButtonTextProperty() {
    return takeInWorkButtonText;
  }

  public StringProperty countTextProperty() {
    return countText;
  }
}