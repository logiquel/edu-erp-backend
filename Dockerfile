# ─────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────
FROM amazoncorretto:25-al2023 AS build
WORKDIR /app

# Install missing tools
RUN yum install -y findutils

# Copy gradle wrapper and config first (for layer caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Set execution permission for gradle wrapper
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build the jar
RUN ./gradlew clean bootJar -x test

# ─────────────────────────────────────
# Stage 2: Run
# ─────────────────────────────────────
FROM amazoncorretto:25-al2023
VOLUME /tmp

# Copy the JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080