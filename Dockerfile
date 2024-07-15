# Use a base image with Java
FROM eclipse-temurin:17 AS build

# Set the working directory
WORKDIR /app

# Add a unique file to ensure unique layers
COPY unique-backend-file.txt .

# Copy the Gradle wrapper and other necessary files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download Gradle dependencies
RUN ./gradlew --no-daemon dependencies

# Copy the rest of the project files
COPY src src

# Build the application
RUN ./gradlew --no-daemon build -x test

# Use a separate image for the runtime
FROM eclipse-temurin:17

# Set the working directory
WORKDIR /app

# Copy the built application from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Copy the application.yml file
COPY src/main/resources/application.yml application.yml

# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "app.jar"]
