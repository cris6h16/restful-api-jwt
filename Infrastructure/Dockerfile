
# Stage 1: Build Stage
FROM maven:3.9-eclipse-temurin-22 as builder
LABEL authors="cris6h16"

# Dir in container ( RUN, COPY, ADD)
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .

# downloads all dependencies and plugins required ( the build isn't gonna require internet connection )
# -B: batch mode; run in a non-interactive mode suitable for automated processes and CI environments.
RUN mvn dependency:go-offline -B

# Copy the source code and build the application ( skip tests )
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production Stage
FROM  openjdk:21-jdk-slim

# non-root user ( privilege escalation attacks ) - Principle of Least Privilege
RUN groupadd appgroup && useradd -g appgroup appuser
USER appuser

WORKDIR /app

# Copy only the necessary artifact from the build stage
COPY --from=builder /app/target/*.jar ./app.jar

# Expose the port for your Spring Boot app
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# JVM optimizations for container environments
# -XX:+UseContainerSupport: allows the JVM to recognize and respect container resource limits
# -Xmx512m: set the maximum heap size to 512MB
# -Xss512k: set the stack size to 512KB ( for each thread ), useful when: many threads or limited memory resources.
ENV JAVA_OPTS="-XX:+UseContainerSupport -Xmx512m -Xss512k"

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
