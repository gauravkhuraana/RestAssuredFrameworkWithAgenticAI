# Use OpenJDK 11 for better REST Assured compatibility
FROM openjdk:11-jdk-slim

# Set working directory
WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven curl && \
    rm -rf /var/lib/apt/lists/*

# Copy pom.xml first for better Docker layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Copy configuration files
COPY *.xml ./

# Create directories for reports and logs
RUN mkdir -p test-output/extent-reports && \
    mkdir -p logs && \
    mkdir -p allure-results

# Set environment variables
ENV MAVEN_OPTS="-Xmx1024m"
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Default environment is dev
ENV env=dev

# Expose port for potential web server (if needed)
EXPOSE 8080

# Default command to run all tests
CMD ["mvn", "clean", "test", "-Denv=${env}"]

# Alternative commands:
# Run smoke tests: docker run --env env=qa <image> mvn clean test -Denv=qa -Psunke
# Run regression tests: docker run --env env=qa <image> mvn clean test -Denv=qa -Pregression
# Run specific test class: docker run <image> mvn clean test -Dtest=UserSmokeTest
