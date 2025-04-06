package com.sensor.sensor_ingestion_service.util;

import com.sensor.sensor_ingestion_service.dto.SensorDataRequest;
import com.sensor.sensor_ingestion_service.dto.SensorDataResponse;
import com.sensor.sensor_ingestion_service.model.SensorData;

public class SensorDataMapper {

    private SensorDataMapper() {
        throw new IllegalStateException("Static utility class");
    }

    public static SensorData fromRequest(SensorDataRequest request) {
        return SensorData.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .temperature(request.getTemperature())
                .build();
    }

    public static SensorDataResponse toResponse(SensorData data) {
        return SensorDataResponse.builder()
                .id(data.getId())
                .timestamp(data.getTimestamp())
                .latitude(data.getLatitude())
                .longitude(data.getLongitude())
                .temperature(data.getTemperature())
                .enriched(data.isEnriched())
                .windSpeed(data.getWindSpeed())
                .windDirection(data.getWindDirection())
                .weatherCode(data.getWeatherCode())
                .weatherDescription(data.getWeatherDescription())
                .build();
    }

}
