package com.sensor.enrichment_service.controller;

import com.sensor.enrichment_service.model.SensorData;
import com.sensor.enrichment_service.service.EnrichmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/enrich")
public class EnrichmentController {

    private final EnrichmentService enrichmentService;

    public EnrichmentController(EnrichmentService enrichmentService) {
        this.enrichmentService = enrichmentService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorData> enrich(@PathVariable String id) {
        return ResponseEntity.ok(enrichmentService.enrichData(id));
    }

}

