# Stage 1: Build the app
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom and source
COPY pom.xml .
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the jar
COPY --from=build /app/target/task-list-0.0.1-SNAPSHOT.jar app.jar

# Install netcat (OpenBSD version)
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy wait-for-it script
COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh

# Expose port
EXPOSE 8080

# Replace 172.17.0.1 with your host machine IP if different
CMD ["/app/wait-for-it.sh", "172.17.0.1:5432", "--", "java", "-jar", "app.jar"]


