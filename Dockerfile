FROM openjdk:11
ADD /target/MoneyStats-2.0.0.jar MoneyStats-2.0.0.jar
CMD ["java","-jar","MoneyStats-2.0.0.jar"]