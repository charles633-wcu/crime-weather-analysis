# =======================
# 1) Build stage
# =======================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy entire repo
COPY . .

# Build ONLY analytics-service (app-service)
RUN mvn -q -DskipTests -pl app-service -am package


# =======================
# 2) Runtime stage
# =======================
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /build/app-service/target/analytics-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
