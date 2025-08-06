FROM eclipse-temurin:21-jdk-alpine

# Install bash just in case
RUN apk add --no-cache bash

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Make sure mvnw is executable (defensive)
RUN chmod +x mvnw

# Build app
RUN ./mvnw -B -DskipTests clean install

# Expose port 3000 (if your app uses it)
EXPOSE 3000

# Run jar
CMD ["sh", "-c", "java -jar target/*.jar"]
