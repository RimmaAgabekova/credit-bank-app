FROM openjdk:17.0.2-jdk-slim-buster
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} calculator.jar
ENTRYPOINT ["java","-jar","/calculator.jar"]