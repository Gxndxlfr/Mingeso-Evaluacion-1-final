FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} evaluacion1-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/evaluacion1-0.0.1-SNAPSHOT.jar"]