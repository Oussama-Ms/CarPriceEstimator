# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17.0.1-jdk-slim

# Create a non-root user (Hugging Face requirement)
RUN useradd -m -u 1000 user
USER user
ENV HOME=/home/user
WORKDIR $HOME/app

# Copy the built jar file and change ownership to our new user
COPY --from=build --chown=user /app/target/AutoValue-1.0-SNAPSHOT.jar app.jar

# Expose the specific Hugging Face port
EXPOSE 7860

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]