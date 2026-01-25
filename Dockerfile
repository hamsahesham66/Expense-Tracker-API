# --- Stage 1: Build the Application ---
# We use a Maven image with Java 17 to match your pom.xml
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application (skipping tests to speed up deployment)
RUN mvn clean package -DskipTests

# --- Stage 2: Run the Application ---
# We use a lightweight Java 17 Runtime (JRE) for the final image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (Render expects this)
EXPOSE 8080

# Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]