# Build app.
FROM adoptopenjdk/openjdk11:ubuntu-slim

WORKDIR /app
COPY target/demo-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "/app/demo-0.0.1-SNAPSHOT.jar"]
