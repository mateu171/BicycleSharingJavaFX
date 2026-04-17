package org.example.bicyclesharing.viewModel.mechanic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.example.bicyclesharing.domain.Impl.Bicycle;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.domain.Impl.MaintenanceRecord;
import org.example.bicyclesharing.domain.Impl.User;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.domain.enums.StateBicycle;
import org.example.bicyclesharing.services.BicycleService;
import org.example.bicyclesharing.services.BikeIssueService;
import org.example.bicyclesharing.services.MaintenanceRecordService;
import org.example.bicyclesharing.util.LocalizationManager;
import org.example.bicyclesharing.viewModel.BaseViewModel;

public class MechanicDashboardViewModel extends BaseViewModel {

  private final BicycleService bicycleService;
  private final BikeIssueService bikeIssueService;
  private final MaintenanceRecordService maintenanceRecordService;

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  public final StringProperty titleText =
      LocalizationManager.getStringProperty("mechanic.dashboard.title");
  public final StringProperty subtitleText =
      LocalizationManager.getStringProperty("mechanic.dashboard.subtitle");

  public final StringProperty newIssuesTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.new_issues");
  public final StringProperty inProgressIssuesTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.in_progress_issues");
  public final StringProperty onMaintenanceTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.on_maintenance");
  public final StringProperty needsInspectionTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.needs_inspection");

  public final StringProperty newIssuesValue = new SimpleStringProperty("...");
  public final StringProperty inProgressIssuesValue = new SimpleStringProperty("...");
  public final StringProperty onMaintenanceValue = new SimpleStringProperty("...");
  public final StringProperty needsInspectionValue = new SimpleStringProperty("...");

  public final StringProperty attentionTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.attention_title");
  public final StringProperty latestActivityTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.latest_activity_title");
  public final StringProperty quickActionsTitle =
      LocalizationManager.getStringProperty("mechanic.dashboard.quick_actions_title");

  public final StringProperty attentionNewIssuesText = new SimpleStringProperty("...");
  public final StringProperty attentionTechnicalIssuesText = new SimpleStringProperty("...");
  public final StringProperty attentionResolvedIssuesText = new SimpleStringProperty("...");
  public final StringProperty attentionMaintenanceRecordsText = new SimpleStringProperty("...");
  public final StringProperty attentionUnavailableBicyclesText = new SimpleStringProperty("...");

  public final StringProperty latestIssueText = new SimpleStringProperty("...");
  public final StringProperty latestMaintenanceText = new SimpleStringProperty("...");
  public final StringProperty latestInspectionText = new SimpleStringProperty("...");

  public final StringProperty openIssuesButtonText =
      LocalizationManager.getStringProperty("mechanic.dashboard.open_issues");
  public final StringProperty openServiceButtonText =
      LocalizationManager.getStringProperty("mechanic.dashboard.open_service");
  public final StringProperty openHistoryButtonText =
      LocalizationManager.getStringProperty("mechanic.dashboard.open_history");

  public final BooleanProperty loading = new SimpleBooleanProperty(false);

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

  public void loadAsync() {
    Task<MechanicDashboardData> task = new Task<>() {
      @Override
      protected MechanicDashboardData call() {
        List<Bicycle> bicycles = bicycleService.getAll();
        List<BikeIssue> issues = bikeIssueService.getAll();
        List<MaintenanceRecord> records = maintenanceRecordService.getAll();

        long newIssues = issues.stream()
            .filter(issue -> issue.getStatus() == IssueStatus.NEW)
            .count();

        long inProgressIssues = issues.stream()
            .filter(issue -> issue.getStatus() == IssueStatus.IN_PROGRESS)
            .count();

        long resolvedIssues = issues.stream()
            .filter(issue -> issue.getStatus() == IssueStatus.RESOLVED)
            .count();

        long technicalIssues = issues.stream()
            .filter(BikeIssue::isTechnicalProblem)
            .count();

        long onMaintenance = bicycles.stream()
            .filter(bicycle -> bicycle.getState() == StateBicycle.ON_MAINTENANCE)
            .count();

        long needsInspection = bicycles.stream()
            .filter(bicycle -> bicycle.getState() == StateBicycle.NEEDS_INSPECTION)
            .count();

        long unavailable = bicycles.stream()
            .filter(bicycle -> bicycle.getState() == StateBicycle.UNAVAILABLE)
            .count();

        long myRecordsCount = records.stream()
            .filter(record -> currentUser != null && currentUser.getId().equals(record.getMechanicId()))
            .count();

        String latestIssue = buildLatestIssueText(issues, bicycles);
        String latestMaintenance = buildLatestMaintenanceText(records, bicycles);
        String latestInspection = buildLatestInspectionText(bicycles);

        return new MechanicDashboardData(
            String.valueOf(newIssues),
            String.valueOf(inProgressIssues),
            String.valueOf(onMaintenance),
            String.valueOf(needsInspection),
            LocalizationManager.getStringByKey("mechanic.dashboard.new_issues") + ": " + newIssues,
            LocalizationManager.getStringByKey("mechanic.dashboard.technical_issues") + ": " + technicalIssues,
            LocalizationManager.getStringByKey("mechanic.dashboard.resolved_issues") + ": " + resolvedIssues,
            LocalizationManager.getStringByKey("mechanic.dashboard.my_records") + ": " + myRecordsCount,
            LocalizationManager.getStringByKey("mechanic.dashboard.unavailable_bicycles") + ": " + unavailable,
            latestIssue,
            latestMaintenance,
            latestInspection
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

    task.setOnFailed(event -> {
      task.getException().printStackTrace();
      loading.set(false);
    });

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();
  }

  private String buildLatestIssueText(List<BikeIssue> issues, List<Bicycle> bicycles) {
    return issues.stream()
        .sorted(Comparator.comparing(BikeIssue::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
        .findFirst()
        .map(issue ->
            LocalizationManager.getStringByKey("mechanic.dashboard.latest_issue")
                + ": "
                + getBicycleModel(issue.getBicycleId(), bicycles)
                + " — "
                + safe(issue.getProblemType())
                + " — "
                + formatDate(issue.getCreatedAt())
        )
        .orElse(LocalizationManager.getStringByKey("mechanic.dashboard.no_data"));
  }

  private String buildLatestMaintenanceText(List<MaintenanceRecord> records, List<Bicycle> bicycles) {
    return records.stream()
        .sorted(Comparator.comparing(MaintenanceRecord::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
        .findFirst()
        .map(record ->
            LocalizationManager.getStringByKey("mechanic.dashboard.latest_maintenance")
                + ": "
                + getBicycleModel(record.getBicycleId(), bicycles)
                + " — "
                + LocalizationManager.getStringByKey(record.getType().getKey())
                + " — "
                + formatDate(record.getCreatedAt())
        )
        .orElse(LocalizationManager.getStringByKey("mechanic.dashboard.no_data"));
  }

  private String buildLatestInspectionText(List<Bicycle> bicycles) {
    return bicycles.stream()
        .filter(bicycle -> bicycle.getState() == StateBicycle.NEEDS_INSPECTION)
        .findFirst()
        .map(bicycle ->
            LocalizationManager.getStringByKey("mechanic.dashboard.latest_inspection")
                + ": "
                + safe(bicycle.getModel())
        )
        .orElse(LocalizationManager.getStringByKey("mechanic.dashboard.no_data"));
  }

  private String getBicycleModel(UUID bicycleId, List<Bicycle> bicycles) {
    if (bicycleId == null) {
      return LocalizationManager.getStringByKey("mechanic.dashboard.unknown_bicycle");
    }

    return bicycles.stream()
        .filter(bicycle -> bicycleId.equals(bicycle.getId()))
        .map(Bicycle::getModel)
        .findFirst()
        .orElse(LocalizationManager.getStringByKey("mechanic.dashboard.unknown_bicycle"));
  }

  private String formatDate(LocalDateTime value) {
    return value == null
        ? LocalizationManager.getStringByKey("mechanic.dashboard.no_data")
        : value.format(formatter);
  }

  private String safe(String value) {
    return value == null || value.isBlank()
        ? LocalizationManager.getStringByKey("mechanic.dashboard.no_data")
        : value;
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