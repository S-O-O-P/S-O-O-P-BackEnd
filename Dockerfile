FROM eclipse-temurin:17
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
# Add a unique layer for backend
ARG BUILD_DATE
RUN echo "backend image $BUILD_DATE" > /unique_layer_backend.txt
CMD ["java","-jar","/app.jar"]
