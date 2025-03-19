FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/DareAndTruth-0.0.1-SNAPSHOT.jar app.jar
COPY serviceAccountKey.json /firebase/serviceAccountKey.json
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]