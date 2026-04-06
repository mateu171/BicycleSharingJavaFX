package org.example.bicyclesharing.viewModel.mechanic;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.Impl.Rental;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.BikeIssueService;
import org.example.bicyclesharing.services.RentalService;
import org.example.bicyclesharing.util.AppConfig;
import org.example.bicyclesharing.util.LocalizationManager;

public class MechanicIssuesViewModel {

  private final BikeIssueService bikeIssueService = AppConfig.bikeIssueService();
  private final BicycleService bicycleService = AppConfig.bicycleService();
  private final RentalService rentalService = AppConfig.rentalService();

  private final ObservableList<BikeIssue> issues = FXCollections.observableArrayList();

  public final StringProperty titleText = LocalizationManager.getStringProperty("mechanic.issues.title");
  public final StringProperty bikeColumnText = LocalizationManager.getStringProperty("mechanic.column.bike");
  public final StringProperty technicalColumnText = LocalizationManager.getStringProperty("mechanic.column.technical");
  public final StringProperty dateColumnText = LocalizationManager.getStringProperty("mechanic.column.date");
  public final StringProperty statusColumnText = LocalizationManager.getStringProperty("mechanic.column.status");
  public final StringProperty searchLabelText = LocalizationManager.getStringProperty("mechanic.search");
  public final StringProperty searchPromptText = LocalizationManager.getStringProperty("mechanic.search.prompt");
  public final StringProperty statusFilterLabelText = LocalizationManager.getStringProperty("mechanic.issues.filter.status");
  public final StringProperty technicalFilterLabelText = LocalizationManager.getStringProperty("mechanic.issues.filter.technical");
  public final StringProperty sortLabelText = LocalizationManager.getStringProperty("mechanic.sort");
  public final StringProperty detailsButtonText = LocalizationManager.getStringProperty("mechanic.button.details");
  public final StringProperty resolveButtonText = LocalizationManager.getStringProperty("mechanic.button.resolve");
  public final StringProperty takeInWorkButtonText = LocalizationManager.getStringProperty("mechanic.button.take");
  public final StringProperty countText = new SimpleStringProperty();

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
  private Rental rental;

  public MechanicIssuesViewModel() {
    loadIssues();
  }

  public ObservableList<BikeIssue> getIssues()
  {
    return issues;
  }

  public void loadIssues()
  {
    issues.setAll(bikeIssueService.getAll());
    updateCount();
  }

  public void updateCount() {
    countText.set(LocalizationManager.getStringByKey("mechanic.count") + ": " + issues.size());
  }

  public String getBikeModel(BikeIssue issue)
  {
    Rental rental = rentalService.getById(issue.getRentalId());
    Bicycle bicycle = bicycleService.getById(rental.getBicycleId()).orElse(null);
    return bicycle != null ? bicycle.getModel() : "";
  }

  public String getProblem(BikeIssue issue)
  {
    return issue.getProblemType();
  }

  public String getComment(BikeIssue issue) {
    return issue.getComment() == null || issue.getComment().isBlank() ? "-" : issue.getComment();
  }

  public String getTechnical(BikeIssue issue) {
    return issue.isTechnicalProblem()
        ? LocalizationManager.getStringByKey("common.yes")
        : LocalizationManager.getStringByKey("common.no");
  }

  public String getDate(BikeIssue issue) {
    return issue.getCreatedAt() == null ? "-" : issue.getCreatedAt().format(formatter);
  }

  public String getStatus(BikeIssue issue) {
    return LocalizationManager.getStringByKey(issue.getStatus().getKey());
  }

  public boolean matchesSearch(BikeIssue issue,String search)
  {
    if(search == null || search.isBlank())
      return true;

    String normalized = search.toLowerCase(Locale.ROOT).trim();
    return safe(getBikeModel(issue)).contains(normalized)
        || safe(getProblem(issue)).contains(normalized)
        || safe(getComment(issue)).contains(normalized)
        || safe(getStatus(issue)).contains(normalized);
  }

  public boolean matchesStatus(BikeIssue issue, String statusFilter) {
    if (statusFilter == null
        || statusFilter.equals(LocalizationManager.getStringByKey("mechanic.filter.all"))) {
      return true;
    }

    return getStatus(issue).equals(statusFilter);
  }

  public boolean matchesTechnical(BikeIssue issue, String technicalFilter) {
    if (technicalFilter == null
        || technicalFilter.equals(LocalizationManager.getStringByKey("mechanic.filter.all"))) {
      return true;
    }

    if (technicalFilter.equals(LocalizationManager.getStringByKey("mechanic.filter.technical"))) {
      return issue.isTechnicalProblem();
    }

    if (technicalFilter.equals(LocalizationManager.getStringByKey("mechanic.filter.nontechnical"))) {
      return !issue.isTechnicalProblem();
    }

    return true;
  }

  public Comparator<BikeIssue> getComparator(String sortValue) {
    String newest = LocalizationManager.getStringByKey("mechanic.sort.newest");
    String oldest = LocalizationManager.getStringByKey("mechanic.sort.oldest");
    String bike = LocalizationManager.getStringByKey("mechanic.sort.bike");
    String status = LocalizationManager.getStringByKey("mechanic.sort.status");

    if (oldest.equals(sortValue)) {
      return Comparator.comparing(BikeIssue::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    if (bike.equals(sortValue)) {
      return Comparator.comparing(this::getBikeModel, String.CASE_INSENSITIVE_ORDER);
    }

    if (status.equals(sortValue)) {
      return Comparator.comparing(this::getStatus, String.CASE_INSENSITIVE_ORDER);
    }

    return Comparator.comparing(BikeIssue::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
  }

  public boolean canTakeInWork(BikeIssue issue) {
    return issue != null && issue.getStatus() == IssueStatus.NEW;
  }

  public boolean canResolve(BikeIssue issue) {
    return issue != null
        && (issue.getStatus() == IssueStatus.NEW || issue.getStatus() == IssueStatus.IN_PROGRESS);
  }

  public void takeInWork(BikeIssue issue) {
    if (!canTakeInWork(issue)) {
      return;
    }

    issue.setStatus(IssueStatus.IN_PROGRESS);
    bikeIssueService.update(issue);

    if (issue.isTechnicalProblem()) {
      Rental rental = rentalService.getById(issue.getRentalId());
      Bicycle bicycle = bicycleService.getById(rental.getBicycleId()).orElse(null);
      if (bicycle != null) {
        bicycle.setState(StateBicycle.ON_MAINTENANCE);
        bicycleService.update(bicycle);
      }
    }

    loadIssues();
  }

  public void resolve(BikeIssue issue) {
    if (!canResolve(issue)) {
      return;
    }

    issue.setStatus(IssueStatus.RESOLVED);
    bikeIssueService.update(issue);

    Rental rental = rentalService.getById(issue.getRentalId());
    if (issue.isTechnicalProblem()) {
      Bicycle bicycle = bicycleService.getById(rental.getBicycleId()).orElse(null);
      if (bicycle != null
          && (bicycle.getState() == StateBicycle.NEEDS_INSPECTION
          || bicycle.getState() == StateBicycle.ON_MAINTENANCE)) {
        bicycle.setState(StateBicycle.AVAILABLE);
        bicycleService.update(bicycle);
      }
    }

    loadIssues();
  }

  private String safe(String value) {
    return value == null ? "" : value.toLowerCase(Locale.ROOT);
  }
}
