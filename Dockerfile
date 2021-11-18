FROM openjdk:11
ADD --from=build /target/moneystats-2.0.0.jar moneystats-2.0.0.jar
CMD ["java","-jar","moneystats-2.0.0.jar"]