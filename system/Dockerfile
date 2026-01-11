FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/system-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]