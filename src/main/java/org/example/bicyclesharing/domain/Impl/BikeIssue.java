package org.example.bicyclesharing.domain.Impl;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.bicyclesharing.domain.enums.IssueStatus;
import org.example.bicyclesharing.exception.CustomEntityValidationExeption;

public class BikeIssue extends BaseEntity {

  private UUID rentalId;
  private UUID bicycleId;
  private UUID userId;
  private String problemType;
  private String comment;
  private boolean technicalProblem;
  private LocalDateTime createdAt;
  private IssueStatus status;

  private BikeIssue() {
    super();
  }

  public BikeIssue(
      UUID rentalId,
      UUID bicycleId,
      UUID userId,
      String problemType,
      String comment,
      boolean technicalProblem
  ) {
    this();
    setRentalId(rentalId);
    setBicycleId(bicycleId);
    setUserId(userId);
    setProblemType(problemType);
    setComment(comment);
    this.technicalProblem = technicalProblem;
    this.createdAt = LocalDateTime.now();
    this.status = IssueStatus.NEW;

    if (!isValid()) {
      throw new CustomEntityValidationExeption(getErrors());
    }
  }

  public static BikeIssue fromDatabase(
      UUID id,
      UUID rentalId,
      UUID bicycleId,
      UUID userId,
      String problemType,
      String comment,
      boolean technicalProblem,
      LocalDateTime createdAt,
      IssueStatus status
  ) {
    BikeIssue issue = new BikeIssue();
    issue.setId(id);
    issue.rentalId = rentalId;
    issue.bicycleId = bicycleId;
    issue.userId = userId;
    issue.problemType = problemType;
    issue.comment = comment;
    issue.technicalProblem = technicalProblem;
    issue.createdAt = createdAt;
    issue.status = status;
    return issue;
  }

  public UUID getRentalId() {
    return rentalId;
  }

  public void setRentalId(UUID rentalId) {
    cleanErrors("rentalId");
    if (rentalId == null) {
      addError("rentalId", "bikeIssue.rental.empty");
    }
    this.rentalId = rentalId;
  }

  public UUID getBicycleId() {
    return bicycleId;
  }

  public void setBicycleId(UUID bicycleId) {
    cleanErrors("bicycleId");
    if (bicycleId == null) {
      addError("bicycleId", "bikeIssue.bicycle.empty");
    }
    this.bicycleId = bicycleId;
  }

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    cleanErrors("userId");
    if (userId == null) {
      addError("userId", "bikeIssue.user.empty");
    }
    this.userId = userId;
  }

  public String getProblemType() {
    return problemType;
  }

  public void setProblemType(String problemType) {
    cleanErrors("problemType");
    if (problemType == null || problemType.trim().isEmpty()) {
      addError("problemType", "bikeIssue.problemType.empty");
    } else if (problemType.trim().length() > 100) {
      addError("problemType", "bikeIssue.problemType.length");
    }
    this.problemType = problemType;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    cleanErrors("comment");
    if (comment != null && comment.length() > 500) {
      addError("comment", "bikeIssue.comment.length");
    }
    this.comment = comment;
  }

  public boolean isTechnicalProblem() {
    return technicalProblem;
  }

  public void setTechnicalProblem(boolean technicalProblem) {
    this.technicalProblem = technicalProblem;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    cleanErrors("createdAt");
    if (createdAt == null) {
      addError("createdAt", "bikeIssue.createdAt.empty");
    }
    this.createdAt = createdAt;
  }

  public IssueStatus getStatus() {
    return status;
  }

  public void setStatus(IssueStatus status) {
    cleanErrors("status");
    if (status == null) {
      addError("status", "bikeIssue.status.empty");
    }
    this.status = status;
  }
}