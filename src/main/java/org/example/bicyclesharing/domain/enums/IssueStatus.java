package org.example.bicyclesharing.domain.enums;

public enum IssueStatus {
  NEW("issue.status.new"),
  IN_PROGRESS("issue.status.in_progress"),
  RESOLVED("issue.status.resolved");

  private final String key;

  IssueStatus(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
