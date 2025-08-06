# Use an official Maven image to build the app
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy everything and build the JAR
COPY . .
RUN mvn clean package -DskipTests

# Use a lightweight JDK image to run the app
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose your desired port
EXPOSE 3000

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
