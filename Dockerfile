FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} msantiagoapp.jar
ENTRYPOINT ["java", "-jar", "/msantiagoapp.jar"]