# Stage 1: Build the Spring Boot app using Maven
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and Maven wrapper files
COPY ./pom.xml /app
COPY ./mvnw /app
COPY ./mvnw.cmd /app
COPY ./.mvn /app/.mvn

# Copy the source code to the container
COPY ./src /app/src

# Ensure the Maven wrapper has execute permissions
RUN chmod +x mvnw

# Build the application using the Maven wrapper, skipping tests
RUN ./mvnw clean package -DskipTests

# Stage 2: Use a lightweight OpenJDK image for running the app
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot will use
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
