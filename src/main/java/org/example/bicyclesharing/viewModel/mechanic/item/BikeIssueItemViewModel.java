package org.example.bicyclesharing.viewModel.mechanic.item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.bicyclesharing.domain.Impl.BikeIssue;

public class BikeIssueItemViewModel {

  private final BikeIssue issue;

  private final StringProperty bikeText =
      new SimpleStringProperty();

  private final StringProperty problemText =
      new SimpleStringProperty();

  private final StringProperty commentText =
      new SimpleStringProperty();

  private final StringProperty technicalText =
      new SimpleStringProperty();

  private final StringProperty dateText =
      new SimpleStringProperty();

  private final StringProperty statusText =
      new SimpleStringProperty();

  public BikeIssueItemViewModel(
      BikeIssue issue,
      String bikeText,
      String problemText,
      String commentText,
      String technicalText,
      String dateText,
      String statusText
  ) {
    this.issue = issue;

    this.bikeText.set(bikeText);
    this.problemText.set(problemText);
    this.commentText.set(commentText);
    this.technicalText.set(technicalText);
    this.dateText.set(dateText);
    this.statusText.set(statusText);
  }

  public BikeIssue issue() {
    return issue;
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

  public StringProperty dateTextProperty() {
    return dateText;
  }

  public StringProperty statusTextProperty() {
    return statusText;
  }
}