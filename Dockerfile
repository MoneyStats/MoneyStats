FROM openjdk:11
FROM mysql:8
EXPOSE 8080
ADD /target/moneystats.jar moneystats.jar
ENTRYPOINT ["java","-jar","moneystats.jar"]