FROM ubuntu:latest
LABEL authors="anshumanrana"



# ─────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────
FROM amazoncorretto:25-al2023 AS build
WORKDIR /app

RUN yum install -y findutils

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew

COPY src src

RUN ./gradlew clean bootJar -x test

# ─────────────────────────────────────
# Stage 2: Run
# ─────────────────────────────────────
FROM amazoncorretto:25-al2023

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", \
  "-Dspring.datasource.url=jdbc:postgresql://db.nvgfnooblleuaaecncnm.supabase.co:5432/postgres", \
  "-Dspring.datasource.username=postgres.nvgfnooblleuaaecncnm", \
  "-Dspring.datasource.password=L@giquel01g3l", \
  "-Dserver.port=8080", \
  "-jar", "/app.jar"]

EXPOSE 8080ENTRYPOINT ["top", "-b"]