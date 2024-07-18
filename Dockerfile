# Dockerfile for Backend
FROM eclipse-temurin:17

# Build argument to force cache busting
ARG CACHEBUST=1

ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar

CMD ["java", "-jar", "/app.jar"]
