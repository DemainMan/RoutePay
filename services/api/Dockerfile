# syntax=docker/dockerfile:1
# ============================================================================
# RoutePay API — multi-stage build.
# Build context MUST be the repo root (multi-module Maven: momo-sdk + api):
#   docker build -f services/api/Dockerfile -t routepay-api .
# ============================================================================

# ---- Stage 1: Build -------------------------------------------------------
FROM eclipse-temurin:17-jdk AS build

# Debian's Maven (3.8.x) is sufficient for this Spring Boot 3.2 project.
RUN apt-get update \
    && apt-get install -y --no-install-recommends maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /build

# Copy POMs first so dependency downloads cache independently of source.
COPY pom.xml ./
COPY packages/momo-sdk/pom.xml packages/momo-sdk/
COPY services/api/pom.xml services/api/

# Copy sources and package. Cache mount keeps ~/.m2 between builds.
COPY packages/momo-sdk packages/momo-sdk
COPY services/api services/api
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q clean package -DskipTests

# ---- Stage 2: Runtime -----------------------------------------------------
FROM eclipse-temurin:17-jre AS runtime

# Non-root user for a smaller attack surface.
RUN useradd --system --uid 1001 routepay

WORKDIR /app
COPY --from=build /build/services/api/target/routepay-api-*.jar app.jar
RUN chown routepay:root app.jar
USER routepay

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
