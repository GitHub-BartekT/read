FROM eclipse-temurin:17-jre-alpine
COPY ./target/read-1.0.6.jar /do-read-app.jar
ENTRYPOINT ["java", "-jar", "/do-read-app.jar"]
