package org.example.bicyclesharing.viewModel.mechanic.modalViewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.BikeIssue;
import org.example.bicyclesharing.util.LocalizationManager;

public class MechanicIssueDetailsViewModel {

  private final StringProperty titleText =
      LocalizationManager.getStringProperty("mechanic.issue.details.title");

  private final StringProperty closeButtonText =
      LocalizationManager.getStringProperty("exit.button");

  private final StringProperty bikeText = new SimpleStringProperty("");
  private final StringProperty problemText = new SimpleStringProperty("");
  private final StringProperty commentText = new SimpleStringProperty("");
  private final StringProperty technicalText = new SimpleStringProperty("");
  private final StringProperty statusText = new SimpleStringProperty("");
  private final StringProperty dateText = new SimpleStringProperty("");

  public void initialize(
      String bike,
      BikeIssue issue,
      String date,
      String status,
      String technical
  ) {
    bikeText.set(
        LocalizationManager.getStringByKey("mechanic.issue.details.bike")
            + ": "
            + safe(bike)
    );

    problemText.set(
        LocalizationManager.getStringByKey("mechanic.issue.details.problem")
            + ": "
            + safe(issue == null ? null : issue.getProblemType())
    );

    commentText.set(
        LocalizationManager.getStringByKey("mechanic.issue.details.comment")
            + ": "
            + safe(issue == null ? null : issue.getComment())
    );

    technicalText.set(
        LocalizationManager.getStringByKey("mechanic.issue.details.technical")
            + ": "
            + safe(technical)
    );

    statusText.set(
        LocalizationManager.getStringByKey("mechanic.issue.details.status")
            + ": "
            + safe(status)
    );

    dateText.set(
        LocalizationManager.getStringByKey("mechanic.issue.details.date")
            + ": "
            + safe(date)
    );
  }

  private String safe(String value) {
    return value == null || value.isBlank() ? "-" : value;
  }

  public StringProperty titleTextProperty() {
    return titleText;
  }

  public StringProperty closeButtonTextProperty() {
    return closeButtonText;
  }

  public StringProperty bikeTextProperty() {
    return bikeText;
  }

  public StringProperty problemTextProperty() {
    return problemText;
  }

  public StringProperty commentTextProperty() {
    return commentText;
  }

  public StringProperty technicalTextProperty() {
    return technicalText;
  }

  public StringProperty statusTextProperty() {
    return statusText;
  }

  public StringProperty dateTextProperty() {
    return dateText;
  }
}