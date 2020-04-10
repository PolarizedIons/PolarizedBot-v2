FROM maven:latest AS builder

WORKDIR /app

COPY ./src/ /app/src
COPY pom.xml /app/pom.xml 

RUN mvn package


FROM openjdk:11.0-jre

WORKDIR /app
COPY --from=builder /app/target/polarizedbot-*-jar-with-dependencies.jar /app/polarizedbot.jar

CMD ["java", "-jar", "/app/polarizedbot.jar"]
