package com.sensor.enrichment_service.service;

import com.sensor.enrichment_service.DTO.WeatherResponseDTO;
import com.sensor.enrichment_service.model.SensorData;
import com.sensor.enrichment_service.repository.ElasticRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
public class EnrichmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentService.class);

    private final ElasticRepo elasticRepo;
    private final String weatherApiBaseUrl;
    private final RestTemplate restTemplate;

    public EnrichmentService(@Value("${weather.api.base-url}") String weatherApiBaseUrl, ElasticRepo elasticRepo) {
        this.weatherApiBaseUrl = weatherApiBaseUrl;
        this.elasticRepo = elasticRepo;
        this.restTemplate = new RestTemplate();
    }

    @Scheduled(fixedRate = 60000)
    public void enrichUnenrichedData() {
        logger.info("Scheduled task triggered to enrich unenriched sensor data");
        try {
            List<SensorData> unenrichedData = elasticRepo.findUnenrichedSensorData();
            logger.info("Found {} unenriched sensor records", unenrichedData.size());

            for (SensorData data : unenrichedData) {
                enrichData(data);
            }

        } catch (IOException e) {
            logger.error("Failed to fetch unenriched sensor data", e);
        }
    }

    public SensorData enrichData(String id) {
        try {
            SensorData data = elasticRepo.getById(id);
            return enrichData(data);
        } catch (IOException e) {
            logger.error("Error retrieving SensorData by ID {}", id, e);
            throw new RuntimeException("Failed to retrieve data: " + e.getMessage(), e);
        }
    }

    public SensorData enrichData(SensorData data) {
        String id = data.getId();

        try {
            URI url = buildWeatherUrl(data);
            logger.info("Calling weather API with URI: {}", url);
            WeatherResponseDTO weatherResponse = restTemplate.getForObject(url, WeatherResponseDTO.class);
            logger.info("Weather API response: {}", weatherResponse);

            if (weatherResponse == null) {
                logger.warn("Weather API returned null for ID {}", id);
                throw new RuntimeException("Invalid API response");
            }

            // Update sensor data with enriched weather data
            data.setWindSpeed(weatherResponse.getWindspeed());
            data.setWindDirection(weatherResponse.getWinddirection());
            data.setWeatherCode(weatherResponse.getWeathercode());
            data.setWeatherDescription(convertWeatherCode(weatherResponse.getWeathercode()));
            data.setEnriched(true);

            // Update sensor data in ElasticRepo
            elasticRepo.updateSensorData(data);
            logger.info("Successfully enriched sensor data for ID: {}", id);

            return data;

        } catch (Exception e) {
            logger.error("Exception while enriching data for ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to enrich sensor data: " + e.getMessage(), e);
        }
    }

    private URI buildWeatherUrl(SensorData data) {
        return UriComponentsBuilder.fromUriString(weatherApiBaseUrl)
                .path("/v1/forecast")
                .queryParam("latitude", data.getLatitude())
                .queryParam("longitude", data.getLongitude())
                .queryParam("current_weather", "true")
                .build()
                .toUri();
    }

    private String convertWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1, 2, 3 -> "Mainly clear, partly cloudy, and overcast";
            case 45, 48 -> "Fog and depositing rime fog";
            case 51, 53, 55 -> "Drizzle";
            case 56, 57 -> "Freezing drizzle";
            case 61, 63, 65 -> "Rain";
            case 66, 67 -> "Freezing rain";
            case 71, 73, 75 -> "Snow fall";
            case 77 -> "Snow grains";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with hail";
            default -> "Unknown weather condition";
        };
    }
}
