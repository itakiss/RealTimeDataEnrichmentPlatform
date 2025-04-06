package com.sensor.enrichment_service.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WeatherResponseDTO {
    private CurrentWeather current_weather;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentWeather {
        private double temperature;
        private double windspeed;
        private double winddirection;
        private int weathercode;
    }

    // Null-safe getters
    public double getWindspeed() {
        return current_weather != null ? current_weather.getWindspeed() : 0.0;
    }

    public double getWinddirection() {
        return current_weather != null ? current_weather.getWinddirection() : 0.0;
    }

    public int getWeathercode() {
        return current_weather != null ? current_weather.getWeathercode() : 0;
    }

    public double getTemperature() {
        return current_weather != null ? current_weather.getTemperature() : 0.0;
    }
}
