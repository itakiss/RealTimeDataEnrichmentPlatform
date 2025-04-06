package com.sensor.sensor_ingestion_service.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorDataRequest {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90.0")
    @Digits(integer = 2, fraction = 4, message = "Latitude must have at most 4 decimal places")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180.0")
    @Digits(integer = 3, fraction = 4, message = "Longitude must have at most 4 decimal places")
    private Double longitude;

    @NotNull(message = "Temperature is required")
    @Digits(integer = 3, fraction = 1, message = "Temperature must have at most one decimal place")
    private Double temperature;
}
