# Use OpenJDK 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Specify the location of the JAR file
ARG JAR_FILE=target/*.jar

# Copy the JAR file into the Docker image
COPY ${JAR_FILE} app.jar

# Set the command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]


EXPOSE 8086