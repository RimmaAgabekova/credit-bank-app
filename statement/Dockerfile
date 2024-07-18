FROM openjdk:17.0.2-jdk-slim-buster
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} statement.jar
ENTRYPOINT ["java","-jar","/statement.jar"]