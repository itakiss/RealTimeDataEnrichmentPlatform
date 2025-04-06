# Sensor Data Platform

A simple microservice architecture for ingesting, enriching, and storing sensor data using Elasticsearch.

---

## Architecture Overview

### Components:

1. ### Elasticsearch (Dockerized)
    - Stores all sensor data.
    - No authentication enabled.

2. ### Sensor Ingestion Service (`sensor_ingestion_service`)
   - Exposes REST API to ingest, retrieve, delete, and list sensor data.
   - Stores raw data in Elasticsearch.

3. ### Enrichment Service (`enrichment_service`)
   - Manually or automatically enriches sensor data with external weather data from Open-Meteo API.
   - Updates existing Elasticsearch documents with enriched data.
   - Automatically enriches unenriched records every **60 seconds** via a scheduled task.

4. ### Kibana
    - For visualization & analytics on sensor data stored in Elasticsearch.

---

## Setup & Deployment Instructions

### Prerequisites:
- Docker & Docker Compose installed.

### Run the entire stack:
```bash
docker-compose up --build
```

# Sensor Data Platform

A microservice-based platform for ingesting, enriching, and visualizing sensor data.

---

## API Endpoints

### Sensor Ingestion Service (Port `8085`)

| Method | Endpoint           | Purpose                      |
|--------|--------------------|------------------------------|
| POST   | `/sensor`          | Ingest new sensor data       |
| GET    | `/sensor/{id}`     | Retrieve sensor data by ID   |
| GET    | `/sensor`          | List all sensor data         |
| DELETE | `/sensor/{id}`     | Delete sensor data by ID     |

### Enrichment Service (Port `8086`)

| Method | Endpoint           | Purpose                                 |
|--------|--------------------|-----------------------------------------|
| PUT    | `/enrich/{id}`     | Enrich a specific sensor record         |
| PUT    | `/enrich-all`      | Enrich all non-enriched records         |

**Note:** Enrichment also runs automatically every 60 seconds for unenriched records.

---

## 🔗 Integration Points

| Service            | External API Used       | Purpose                                      |
|--------------------|-------------------------|----------------------------------------------|
| Enrichment Service | [Open-Meteo API](https://open-meteo.com/en/docs) | Fetches weather data (wind, code, description) to enrich data |

---

## Running Kibana

Once the stack is running, you can access Kibana at:

**[http://localhost:5601](http://localhost:5601)**

Use Kibana to visualize your sensor data using **Elasticsearch index patterns**.

---

## Tech Stack

- Java 21
- Spring Boot 3.4.4
- Elasticsearch 8.17.4
- Kibana 8.17.4
- Docker / Docker Compose
- [Open-Meteo API](https://open-meteo.com/en/docs)

---
## Notes

All data is stored in the weather index in Elasticsearch.

The enrichment service uses Open-Meteo’s current_weather=true flag to enrich data points with real-time wind, direction, and weather codes.

Weather codes are translated into human-readable descriptions before storing.

