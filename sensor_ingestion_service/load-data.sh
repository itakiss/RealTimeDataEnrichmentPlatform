#!/bin/sh


if curl -s -o /dev/null -w "%{http_code}" -X GET "http://elasticsearch:9200/weather" | grep -q "200"; then
  echo "Index 'weather' already exists. Skipping creation."
else
  echo "Creating index 'weather' with mappings..."
  curl -X PUT "http://elasticsearch:9200/weather" -H "Content-Type: application/json" -d '{
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 1
    },
    "mappings": {
      "properties": {
        "_id":{"type":"string"},
        "latitude": { "type": "float" },
        "longitude": { "type": "float" },
        "temperature": { "type": "float" },
        "windSpeed": { "type": "float" },
        "windDirection": { "type": "integer" },
        "weatherCode": { "type": "integer" },
        "weatherDescription": { "type": "text" },
        "timestamp": { "type": "date", "format": "strict_date_optional_time||epoch_millis" }
      }
    }
  }'
fi

echo "Loading initial data into Elasticsearch..."
curl -X POST "http://elasticsearch:9200/weather/_bulk" -H "Content-Type: application/json" --data-binary @init-data.ndjson

echo "Data loading complete."
