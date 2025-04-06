package com.sensor.sensor_ingestion_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorDataResponse {

    private String id;
    private String timestamp;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Boolean enriched;
    private Double windSpeed;
    private Double windDirection;
    private Integer weatherCode;
    private String weatherDescription;

}
