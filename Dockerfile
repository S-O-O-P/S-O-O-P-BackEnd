FROM eclipse-temurin:17
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
# Add a unique layer for backend
RUN echo "backend image" > /unique_layer.txt
CMD ["java","-jar","/app.jar"]

