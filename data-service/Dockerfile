# =======================
# 1) Build stage
# =======================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy entire repo (parent pom + all modules)
COPY . .

# Build ONLY data-service + dependencies
RUN mvn -q -DskipTests -pl data-service -am package


# =======================
# 2) Runtime stage
# =======================
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the built JAR
COPY --from=build /build/data-service/target/data-service-1.0-SNAPSHOT.jar app.jar

# SQLite DB will be mounted here
VOLUME ["/data"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
