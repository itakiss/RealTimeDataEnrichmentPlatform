package com.sensor.enrichment_service.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.sensor.enrichment_service.model.SensorData;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class ElasticRepo {

    private final ElasticsearchClient elasticsearchClient;

    public ElasticRepo(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<SensorData> findUnenrichedSensorData() throws IOException {
        var response = elasticsearchClient.search(
                s -> s.index("weather")
                        .query(q -> q.term(t -> t.field("enriched")
                                .value(v -> v.booleanValue(false)))),
                SensorData.class);

        return response.hits().hits().stream().map(Hit::source).toList();
    }

    public SensorData getById(String id) throws IOException {
        var response = elasticsearchClient.get(
                g -> g.index("weather")
                        .id(id),
                SensorData.class);

        if (!response.found()) {
            throw new RuntimeException("SensorData with ID " + id + " not found");
        }

        return response.source();
    }

    public void updateSensorData(SensorData data) throws IOException {
        IndexResponse response = elasticsearchClient.index(
                i -> i.index("weather")
                        .id(data.getId())
                        .document(data));

        if (!"Updated".equalsIgnoreCase(response.result().toString())) {
            throw new RuntimeException("Failed to update enriched data in Elasticsearch");
        }
    }
}
