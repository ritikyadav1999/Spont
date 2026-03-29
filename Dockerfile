# Use Java 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy everything
COPY . .

# Build the app
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/Spont-0.0.1-SNAPSHOT.jar"]