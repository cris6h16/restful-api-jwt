
# Stage 1: Build Stage
FROM maven:3.9-eclipse-temurin-22 as builder
LABEL authors="cris6h16"

WORKDIR /app

COPY /pom.xml .
COPY /Infrastructure/ ./Infrastructure/
COPY /Application/ ./Application/
COPY /Domain/ ./Domain/

# Download dependencies before building
RUN mvn dependency:go-offline -B

#
RUN mvn clean package -DskipTests

# Stage 2: Production Stage
FROM  openjdk:21-jdk-slim

# non-root user ( privilege escalation attacks ) - Principle of Least Privilege
RUN groupadd appgroup && useradd -g appgroup appuser
USER appuser

WORKDIR /app

# Copy only the necessary artifact from the build stage
COPY --from=builder /app/Infrastructure/target/*.jar ./app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
