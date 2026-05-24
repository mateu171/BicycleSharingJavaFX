package org.example.bicyclesharing.dto;

import java.time.LocalDateTime;

public record LatestRentalInfo(
    String customerName,
    String bicycleModel,
    LocalDateTime start
) {}