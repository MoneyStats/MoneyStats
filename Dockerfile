FROM openjdk:11
FROM mysql:8
ADD /home/runner/work/MoneyStats/MoneyStats/target/moneystats-2.0.0.jar moneystats-2.0.0.jar
CMD ["java","-jar","moneystats-2.0.0.jar"]