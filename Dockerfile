# Use the Eclipse Temurin Alpine image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy files
COPY . .

# Make mvnw executable (VERY IMPORTANT)
RUN chmod +x mvnw

# Build the project
RUN mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install

# Run the jar (adjust the name if needed)
CMD ["sh", "-c", "java -jar target/*.jar"]
