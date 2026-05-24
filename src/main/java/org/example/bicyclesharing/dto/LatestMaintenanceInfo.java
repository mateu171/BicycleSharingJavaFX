package org.example.bicyclesharing.dto;

import java.time.LocalDateTime;

public record LatestMaintenanceInfo(
    String bicycleModel,
    String maintenanceTypeKey,
    LocalDateTime createdAt
) {
}
