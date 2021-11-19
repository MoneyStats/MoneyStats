FROM openjdk:11
EXPOSE 8080
COPY /target/moneystats.jar moneystats.jar
ENTRYPOINT ["java","-jar","moneystats.jar"]