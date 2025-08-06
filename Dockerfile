# Step 1: Build the app using Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the app using a lightweight JDK
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Set the port your app runs on
ENV PORT=3000
EXPOSE 3000

# Start the application
CMD ["java", "-jar", "app.jar"]
