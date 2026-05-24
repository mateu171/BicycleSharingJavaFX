package org.example.bicyclesharing.viewModel.mechanic;

import java.time.format.DateTimeFormatter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.dto.LatestInspectionInfo;
import org.example.bicyclesharing.dto.LatestIssueInfo;
import org.example.bicyclesharing.dto.LatestMaintenanceInfo;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.BikeIssueService;
import org.example.bicyclesharing.services.MaintenanceRecordService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class MechanicDashboardViewModel extends BaseViewModel {

  private final BicycleService bicycleService;
  private final BikeIssueService bikeIssueService;
  private final MaintenanceRecordService maintenanceRecordService;

  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("mechanic.dashboard.title");

  private final StringProperty subtitleText =
      LocalizationManager.getStringProperty("mechanic.dashboard.subtitle");

  private final StringProperty newIssuesTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.new_issues");

  private final StringProperty inProgressIssuesTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.in_progress_issues");

  private final StringProperty onMaintenanceTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.on_maintenance");

  private final StringProperty needsInspectionTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.needs_inspection");

  private final StringProperty newIssuesValue =
      new SimpleStringProperty("...");

  private final StringProperty inProgressIssuesValue =
      new SimpleStringProperty("...");

  private final StringProperty onMaintenanceValue =
      new SimpleStringProperty("...");

  private final StringProperty needsInspectionValue =
      new SimpleStringProperty("...");

  private final StringProperty attentionTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.attention_title");

  private final StringProperty latestActivityTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.latest_activity_title");

  private final StringProperty quickActionsTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.quick_actions_title");

  private final StringProperty attentionNewIssuesText =
      new SimpleStringProperty("...");

  private final StringProperty attentionTechnicalIssuesText =
      new SimpleStringProperty("...");

  private final StringProperty attentionResolvedIssuesText =
      new SimpleStringProperty("...");

  private final StringProperty attentionMaintenanceRecordsText =
      new SimpleStringProperty("...");

  private final StringProperty attentionUnavailableBicyclesText =
      new SimpleStringProperty("...");

  private final StringProperty latestIssueText =
      new SimpleStringProperty("...");

  private final StringProperty latestMaintenanceText =
      new SimpleStringProperty("...");

  private final StringProperty latestInspectionText =
      new SimpleStringProperty("...");

  private final StringProperty openIssuesButtonText =
      LocalizationManager.getStringProperty("mechanic.dashboard.open_issues");

  private final StringProperty openServiceButtonText =
      LocalizationManager.getStringProperty("mechanic.dashboard.open_service");

  private final StringProperty openHistoryButtonText =
      LocalizationManager.getStringProperty("mechanic.dashboard.open_history");

  private final BooleanProperty loading =
      new SimpleBooleanProperty(false);

  public MechanicDashboardViewModel(
      User currentUser,
      BicycleService bicycleService,
      BikeIssueService bikeIssueService,
      MaintenanceRecordService maintenanceRecordService
  ) {
    super(currentUser);
    this.bicycleService = bicycleService;
    this.bikeIssueService = bikeIssueService;
    this.maintenanceRecordService = maintenanceRecordService;
  }

  public void initialize() {
    loadAsync();
  }

  private void loadAsync() {
    Task<MechanicDashboardData> task = new Task<>() {
      @Override
      protected MechanicDashboardData call() {
        long newIssues = bikeIssueService.countByIssueStatus(IssueStatus.NEW);

        long inProgressIssues = bikeIssueService.countByIssueStatus(IssueStatus.IN_PROGRESS);

        long resolvedIssues = bikeIssueService.countByIssueStatus(IssueStatus.RESOLVED);

        long technicalIssues = bikeIssueService.countTechnicalIssues();

        long onMaintenance = bicycleService.countByState(StateBicycle.ON_MAINTENANCE);

        long needsInspection = bicycleService.countByState(StateBicycle.NEEDS_INSPECTION);

        long unavailable = bicycleService.countByState(StateBicycle.UNAVAILABLE);

        long myRecordsCount = maintenanceRecordService.countByMechanicId(currentUser.getId());

        LatestIssueInfo latestIssueInfo = bikeIssueService.getLatestIssueInfo();
        LatestMaintenanceInfo latestMaintenanceInfo = maintenanceRecordService.getLatestMaintenanceInfo();
        LatestInspectionInfo latestInspectionInfo = bicycleService.getLatestInspectionInfo();

        return new MechanicDashboardData(
            String.valueOf(newIssues),
            String.valueOf(inProgressIssues),
            String.valueOf(onMaintenance),
            String.valueOf(needsInspection),

            LocalizationManager.getStringByKey("mechanic.dashboard.new_issues")
                + ": " + newIssues,

            LocalizationManager.getStringByKey("mechanic.dashboard.technical_issues")
                + ": " + technicalIssues,

            LocalizationManager.getStringByKey("mechanic.dashboard.resolved_issues")
                + ": " + resolvedIssues,

            LocalizationManager.getStringByKey("mechanic.dashboard.my_records")
                + ": " + myRecordsCount,

            LocalizationManager.getStringByKey("mechanic.dashboard.unavailable_bicycles")
                + ": " + unavailable,

            buildLatestIssueText(latestIssueInfo),
            buildLatestMaintenanceText(latestMaintenanceInfo),
            buildLatestInspectionText(latestInspectionInfo)
        );
      }
    };

    loading.set(true);

    task.setOnSucceeded(event -> {
      MechanicDashboardData data = task.getValue();

      newIssuesValue.set(data.newIssuesValue());
      inProgressIssuesValue.set(data.inProgressIssuesValue());
      onMaintenanceValue.set(data.onMaintenanceValue());
      needsInspectionValue.set(data.needsInspectionValue());

      attentionNewIssuesText.set(data.attentionNewIssuesText());
      attentionTechnicalIssuesText.set(data.attentionTechnicalIssuesText());
      attentionResolvedIssuesText.set(data.attentionResolvedIssuesText());
      attentionMaintenanceRecordsText.set(data.attentionMaintenanceRecordsText());
      attentionUnavailableBicyclesText.set(data.attentionUnavailableBicyclesText());

      latestIssueText.set(data.latestIssueText());
      latestMaintenanceText.set(data.latestMaintenanceText());
      latestInspectionText.set(data.latestInspectionText());

      loading.set(false);
    });

    task.setOnFailed(event -> loading.set(false));

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }

  private String buildLatestIssueText(
      LatestIssueInfo issue
  ) {

    if (issue == null) {
      return LocalizationManager.getStringByKey(
          "mechanic.dashboard.no_data"
      );
    }

    return LocalizationManager.getStringByKey(
        "mechanic.dashboard.latest_issue"
    )
        + ": "
        + issue.bicycleModel()
        + " — "
        + safe(issue.problemType())
        + " — "
        + issue.createdAt().format(formatter);
  }

  private String buildLatestMaintenanceText(
      LatestMaintenanceInfo maintenance
  ) {

    if (maintenance == null) {
      return LocalizationManager.getStringByKey(
          "mechanic.dashboard.no_data"
      );
    }

    return LocalizationManager.getStringByKey(
        "mechanic.dashboard.latest_maintenance"
    )
        + ": "
        + maintenance.bicycleModel()
        + " — "
        + LocalizationManager.getStringByKey(
        maintenance.maintenanceTypeKey()
    )
        + " — "
        + maintenance.createdAt().format(formatter);
  }

  private String buildLatestInspectionText(
      LatestInspectionInfo inspection
  ) {

    if (inspection == null) {
      return LocalizationManager.getStringByKey(
          "mechanic.dashboard.no_data"
      );
    }

    return LocalizationManager.getStringByKey(
        "mechanic.dashboard.latest_inspection"
    )
        + ": "
        + safe(inspection.bicycleModel());
  }

  private String safe(String value) {
    return value == null || value.isBlank()
        ? LocalizationManager.getStringByKey("mechanic.dashboard.no_data")
        : value;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty subtitleTextProperty() {
    return subtitleText;
  }

  public StringProperty newIssuesTitleProperty() {
    return newIssuesTitle;
  }

  public StringProperty inProgressIssuesTitleProperty() {
    return inProgressIssuesTitle;
  }

  public StringProperty onMaintenanceTitleProperty() {
    return onMaintenanceTitle;
  }

  public StringProperty needsInspectionTitleProperty() {
    return needsInspectionTitle;
  }

  public StringProperty newIssuesValueProperty() {
    return newIssuesValue;
  }

  public StringProperty inProgressIssuesValueProperty() {
    return inProgressIssuesValue;
  }

  public StringProperty onMaintenanceValueProperty() {
    return onMaintenanceValue;
  }

  public StringProperty needsInspectionValueProperty() {
    return needsInspectionValue;
  }

  public StringProperty attentionTitleProperty() {
    return attentionTitle;
  }

  public StringProperty latestActivityTitleProperty() {
    return latestActivityTitle;
  }

  public StringProperty quickActionsTitleProperty() {
    return quickActionsTitle;
  }

  public StringProperty attentionNewIssuesTextProperty() {
    return attentionNewIssuesText;
  }

  public StringProperty attentionTechnicalIssuesTextProperty() {
    return attentionTechnicalIssuesText;
  }

  public StringProperty attentionResolvedIssuesTextProperty() {
    return attentionResolvedIssuesText;
  }

  public StringProperty attentionMaintenanceRecordsTextProperty() {
    return attentionMaintenanceRecordsText;
  }

  public StringProperty attentionUnavailableBicyclesTextProperty() {
    return attentionUnavailableBicyclesText;
  }

  public StringProperty latestIssueTextProperty() {
    return latestIssueText;
  }

  public StringProperty latestMaintenanceTextProperty() {
    return latestMaintenanceText;
  }

  public StringProperty latestInspectionTextProperty() {
    return latestInspectionText;
  }

  public StringProperty openIssuesButtonTextProperty() {
    return openIssuesButtonText;
  }

  public StringProperty openServiceButtonTextProperty() {
    return openServiceButtonText;
  }

  public StringProperty openHistoryButtonTextProperty() {
    return openHistoryButtonText;
  }

  public BooleanProperty loadingProperty() {
    return loading;
  }

  private record MechanicDashboardData(
      String newIssuesValue,
      String inProgressIssuesValue,
      String onMaintenanceValue,
      String needsInspectionValue,
      String attentionNewIssuesText,
      String attentionTechnicalIssuesText,
      String attentionResolvedIssuesText,
      String attentionMaintenanceRecordsText,
      String attentionUnavailableBicyclesText,
      String latestIssueText,
      String latestMaintenanceText,
      String latestInspectionText
  ) {
  }
}