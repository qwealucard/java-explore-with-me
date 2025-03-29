FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY ../target/stats-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]