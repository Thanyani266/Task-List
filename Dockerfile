# ========================
# Stage 1: Build the app
# ========================
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar (skip tests for faster build)
RUN mvn clean package -DskipTests


# ========================
# Stage 2: Runtime image
# ========================
FROM eclipse-temurin:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/task-list-0.0.1-SNAPSHOT.jar app.jar

# Expose the Spring Boot app port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
