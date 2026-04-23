FROM ubuntu:latest
LABEL authors="anshumanrana"

ENTRYPOINT ["top", "-b"]

# ---- Stage 1: Build ----
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Copy gradle wrapper and config first (for layer caching)
COPY gradlew gradlew.bat* ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./

# Download dependencies (cached unless build files change)
RUN ./gradlew dependencies --no-daemon || true

# Copy source and build
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port (match your app's port)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]