FROM eclipse-temurin:17
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
RUN echo "backend image $(date)" > /unique_layer.txt
# Add unique layer with a build argument
ARG BACKEND_VERSION
RUN echo "Backend version: $BACKEND_VERSION" > /backend_version.txt
CMD ["java","-jar","/app.jar"]
