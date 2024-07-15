FROM eclipse-temurin:17
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
# Add a unique layer for backend admin
RUN echo "backend image $(date)" > /unique_layer.txt
CMD ["java","-jar","/app.jar"]

