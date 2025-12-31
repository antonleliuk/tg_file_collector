# Stage: Build Stage
FROM maven:3-eclipse-temurin-25-alpine AS build
WORKDIR /app

COPY .git ./.git

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B -U

# Copy only the source code
COPY src ./src

# Build the application with layers
RUN mvn clean package -DskipTests

# Stage: Extract layers
FROM eclipse-temurin:25-jre-alpine-3.23 AS layers
WORKDIR /app

COPY --from=build /app/target/*.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# Stage: Runtime Stage
FROM eclipse-temurin:25-jre-alpine-3.23
WORKDIR /app

# Add a non-root user for better security
# Install curl
# hadolint ignore=DL3018
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup && apk add --no-cache curl

# Set the user to the newly created non-root user
USER appuser

# Copy over only required layers for runtime
COPY --from=layers /app/extracted/dependencies/ /app/extracted/spring-boot-loader/ /app/extracted/snapshot-dependencies/ /app/extracted/application/ ./

# Add a HEALTHCHECK instruction to ensure the container's health
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1
# Expose the application port (adjust if necessary)
EXPOSE 8080

# Define the entry point
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar application.jar"]
