FROM openjdk:11
FROM mysql:8
ADD /target/moneystats.jar moneystats.jar
CMD ["java","-jar","moneystats.jar"]