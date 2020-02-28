FROM maven:3.6-jdk-11 AS BUILD 

COPY /src /app/src

COPY pom.xml /app/pom.xml 

RUN mvn -f /app/pom.xml install


FROM openjdk:11.0.4-jre

RUN java -version

COPY --from=BUILD /app/target/exchange-0.0.1-SNAPSHOT.jar /exchange.jar
COPY entrypoint.sh /

EXPOSE 8080

ENTRYPOINT sh entrypoint.sh