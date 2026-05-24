package org.example.bicyclesharing.dto;

import java.time.LocalDateTime;

public record LatestReservationInfo(
    String customerName,
    String bicycleModel,
    LocalDateTime start
) {}