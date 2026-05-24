package org.example.bicyclesharing.dto;

import java.time.LocalDateTime;

public record LatestIssueInfo(
    String bicycleModel,
    String problemType,
    LocalDateTime createdAt
) {}