FROM openjdk:8-alpine
COPY build/libs/voting-manager-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app/
CMD ["java", "-Dspring.data.mongodb.uri=mongodb://mongodb:27017/voting-manager-db", "-jar", "voting-manager-0.0.1-SNAPSHOT.jar"]