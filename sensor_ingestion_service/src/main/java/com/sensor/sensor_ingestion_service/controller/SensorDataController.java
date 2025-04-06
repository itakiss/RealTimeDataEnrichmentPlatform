package com.sensor.sensor_ingestion_service.controller;

import com.sensor.sensor_ingestion_service.dto.SensorDataRequest;
import com.sensor.sensor_ingestion_service.dto.SensorDataResponse;
import com.sensor.sensor_ingestion_service.model.SensorData;
import com.sensor.sensor_ingestion_service.service.SaveStatus;
import com.sensor.sensor_ingestion_service.service.SensorDataService;
import com.sensor.sensor_ingestion_service.util.SensorDataMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sensor-data")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @PostMapping
    public ResponseEntity<?> saveSensorData(@Valid @RequestBody SensorDataRequest sensorDataRequest) {
        SensorData sensorData = SensorDataMapper.fromRequest(sensorDataRequest);

        SaveStatus status = sensorDataService.save(sensorData);

        return switch (status) {
            case SUCCESS -> ResponseEntity.ok(SensorDataMapper.toResponse(sensorData));
            case DUPLICATE_LOCATION -> ResponseEntity.status(409).body("Duplicate latitude and longitude found.");
            case ERROR -> ResponseEntity.internalServerError().body("Error saving sensor data.");
        };
    }


    @GetMapping("/{id}")
    public ResponseEntity<SensorDataResponse> getSensorDataById(@PathVariable String id) {

        SensorData data = sensorDataService.getById(id);
        return Optional.ofNullable(data)
                .map(SensorDataMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        List<SensorData> dataList = sensorDataService.getAll();
        return ResponseEntity.ok(dataList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSensorDataById(@PathVariable String id) {
        boolean isDeleted = sensorDataService.deleteById(id);
        if (isDeleted) {
            return ResponseEntity.ok("Sensor data deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
