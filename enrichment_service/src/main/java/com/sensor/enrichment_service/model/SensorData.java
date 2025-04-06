package com.sensor.enrichment_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorData {

    private String id;
    private double latitude;
    private double longitude;
    private double temperature;
    private double windSpeed;
    private boolean enriched;
    private double windDirection;
    private int weatherCode;
    private String weatherDescription;

}
