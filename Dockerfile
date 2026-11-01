# Stage 1: Build the application using Maven
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper or build files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Package the application (skip tests if already verified locally)
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application using a lightweight JRE image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]