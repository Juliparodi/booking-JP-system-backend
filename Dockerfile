# Stage 1: Build compilation
FROM maven:3.9.9-eclipse-temurin-25-alpine AS build
WORKDIR /build

# Copy Maven descriptor and fetch offline dependencies to speed up subsequent builds (caching layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy project files and build the production bundle
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimalist production JRE runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Create a secure, non-root system user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy built artifact from build stage and assign ownership to appuser
COPY --from=build --chown=appuser:appgroup /build/target/booking-JP-system-backend-*.jar app.jar

# Enforce secure non-root environment execution
USER appuser

# Expose server listening port
EXPOSE 8080

# Environment configurations
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Optimized JVM memory and Garbage Collector parameters for containerization
ENTRYPOINT ["java", \
            "-XX:+UseG1GC", \
            "-XX:+UseStringDeduplication", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", \
            "app.jar"]
