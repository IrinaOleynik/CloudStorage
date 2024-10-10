FROM openjdk:17
EXPOSE 8090
ADD target/CloudStorage-0.0.1-SNAPSHOT.jar CloudStorage.jar
ENTRYPOINT ["java", "-jar", "CloudStorage.jar"]