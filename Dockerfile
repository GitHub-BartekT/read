FROM eclipse-temurin:17-jre-alpine
COPY ./target/readapplication.jar /do-read-app.jar
ENTRYPOINT ["java", "-jar", "/do-read-app.jar"]
