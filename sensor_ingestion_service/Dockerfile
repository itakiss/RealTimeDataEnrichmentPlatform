FROM eclipse-temurin:21-jdk-alpine

#RUN apk add --no-cache curl

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

#COPY load-data.sh /load-data.sh
#COPY init-data.ndjson /init-data.ndjson

#RUN chmod +x /load-data.sh

#CMD ["/bin/sh", "-c", "/load-data.sh && java -jar /app.jar"]

EXPOSE 8085

