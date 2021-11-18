FROM openjdk:11
FROM mysql:8
ADD /target/moneystats-2.0.0.jar moneystats-2.0.0.jar
CMD ["java","-jar","moneystats-2.0.0.jar"]