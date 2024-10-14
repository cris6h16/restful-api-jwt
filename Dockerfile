# Stage 0: Generate SSL Certificates
FROM openjdk:21-jdk-slim as ssl

# SSL
ARG STOREPASS
ARG KEYPASS
# generate, export and import the certificate into the truststore
RUN keytool -genkeypair -alias cris6h16app \
    -keyalg RSA -keystore cris6h16app-keystore.jks -keysize 2048 \
    -dname "CN=Cristian Herrera, OU=cris6h16, O=cris6h16, L=Tena, ST=Napo, C=Ecuador" \
    -storepass $STOREPASS -keypass $KEYPASS -validity 365 && \
    keytool -exportcert -alias cris6h16app \
    -keystore cris6h16app-keystore.jks -file cris6h16app.crt \
    -storepass $STOREPASS && \
    keytool -importcert -alias cris6h16app-cert \
    -file cris6h16app.crt -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit -noprompt

# Stage 1: Build Stage
FROM maven:3.9-eclipse-temurin-22 as builder
LABEL authors="cris6h16"

WORKDIR /app

COPY /pom.xml .
COPY /Infrastructure/ ./Infrastructure/
COPY /Application/ ./Application/
COPY /Domain/ ./Domain/

COPY --from=ssl /cris6h16app-keystore.jks ./Infrastructure/src/main/resources/cris6h16app-keystore.jks

# Download dependencies before building
RUN mvn dependency:go-offline -B

#
RUN mvn clean package -DskipTests

# Stage 2: Production Stage
FROM  openjdk:21-jdk-slim

WORKDIR /app

COPY --from=builder /app/Infrastructure/target/*.jar ./app.jar
COPY --from=ssl /usr/local/openjdk-21/lib/security/cacerts $JAVA_HOME/lib/security/cacerts

# non-root user ( privilege escalation attacks ) - Principle of Least Privilege
RUN groupadd appgroup && useradd -g appgroup appuser
USER appuser


EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
