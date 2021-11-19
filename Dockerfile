FROM openjdk:11
FROM mysql:8
ADD /home/runner/work/MoneyStats/MoneyStats/target/moneystats.jar moneystats.jar
CMD ["java","-jar","moneystats.jar"]