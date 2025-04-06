package com.sensor.sensor_ingestion_service.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.sensor.sensor_ingestion_service.model.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensorDataService {

    private static final Logger logger = LoggerFactory.getLogger(SensorDataService.class);

    private final ElasticsearchClient elasticsearchClient;

    public SensorDataService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
        ensureIndexExists();
    }

    public SaveStatus save(SensorData sensorData) {
        try {
            logger.info("Attempting to save sensor data: {}", sensorData);

            if (existsByLatLon(sensorData.getLatitude(), sensorData.getLongitude())) {
                logger.warn(
                        "Duplicate sensor data found: latitude={}, longitude={}",
                        sensorData.getLatitude(),
                        sensorData.getLongitude());
                return SaveStatus.DUPLICATE_LOCATION;
            }

            elasticsearchClient.index(
                    i -> i.index("weather")
                            .id(sensorData.getId()).document(sensorData));

            logger.info("Sensor data saved successfully with ID: {}", sensorData.getId());
            return SaveStatus.SUCCESS;

        } catch (IOException e) {
            logger.error("Error saving sensor data: ", e);
            return SaveStatus.ERROR;
        }
    }

    public SensorData getById(String id) {
        try {
            logger.info("Fetching sensor data with ID: {}", id);

            GetResponse<SensorData> response = elasticsearchClient.get(
                    g -> g.index("weather")
                            .id(id),
                    SensorData.class);

            if (response.found()) {
                logger.info("Sensor data found for ID: {}", id);
                return response.source();
            } else {
                logger.warn("Sensor data not found for ID: {}", id);
                return null;
            }
        } catch (IOException e) {
            logger.error("Error fetching sensor data: ", e);
            return null;
        }
    }

    public List<SensorData> getAll() {
        try {
            SearchResponse<SensorData> searchResponse = elasticsearchClient.search(
                    s -> s.index("weather")
                            .query(q -> q.matchAll(m -> m))
                            .size(100),
                    SensorData.class);

            return searchResponse.hits()
                    .hits()
                    .stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            logger.error("Error fetching all sensor data: ", e);
            return Collections.emptyList();
        }
    }

    public boolean deleteById(String id) {
        try {
            logger.info("Attempting to delete sensor data with ID: {}", id);

            // Try to delete the document from Elasticsearch
            elasticsearchClient.delete(d -> d.index("weather").id(id));

            logger.info("Sensor data deleted successfully with ID: {}", id);
            return true;
        } catch (IOException e) {
            logger.error("Error deleting sensor data: ", e);
            return false;
        }
    }

    private void ensureIndexExists() {
        try {

            boolean indexExists = elasticsearchClient.indices()
                    .exists(e -> e.index("weather"))
                    .value();

            if (!indexExists) {
                elasticsearchClient.indices().create(c -> c.index("weather"));
                logger.info("Index 'weather' created successfully.");
            } else {
                logger.info("Index 'weather' already exists.");
            }
        } catch (IOException e) {
            logger.error("Error checking or creating index: ", e);
        }
    }

    private boolean existsByLatLon(Double latitude, Double longitude) {
        try {
            logger.info("Checking existence for latitude: {}, longitude: {}", latitude, longitude);

            SearchResponse<SensorData> searchResponse = elasticsearchClient.search(
                    s -> s.index("weather")
                            .query(q -> q.bool(b -> b.must(m -> m.match(t -> t.field("latitude").
                                    query(latitude))).
                                    must(m -> m.match(t -> t.field("longitude")
                                            .query(longitude)))))
                            .size(1),
                    SensorData.class);

            List<Hit<SensorData>> hits = searchResponse.hits().hits();
            boolean exists = !hits.isEmpty();

            logger.info("Existence check result: {}", exists);
            return exists;

        } catch (IOException e) {
            logger.error("Error checking existence: ", e);
            return false;
        }
    }

}
