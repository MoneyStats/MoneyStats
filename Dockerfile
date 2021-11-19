FROM openjdk:11
# Docker Port
#EXPOSE 8080
# Docker Dev Port
EXPOSE 8000
COPY /target/moneystats.jar moneystats.jar
ENTRYPOINT ["java","-jar","moneystats.jar"]