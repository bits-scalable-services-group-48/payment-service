# Base image
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy gradle wrapper
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src

# Build the jar
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# Copy built jar
RUN cp build/libs/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
