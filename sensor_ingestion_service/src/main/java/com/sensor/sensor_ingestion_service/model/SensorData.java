package com.sensor.sensor_ingestion_service.model;

import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorData {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Builder.Default
    private String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

    private Double latitude;
    private Double longitude;
    private Double temperature;

    private Double windSpeed;
    private Double windDirection;
    private Integer weatherCode;
    private String weatherDescription;

    @Builder.Default
    private boolean enriched = false;

}
