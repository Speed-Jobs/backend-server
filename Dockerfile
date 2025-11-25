FROM eclipse-temurin:21-jre

WORKDIR /app

EXPOSE 8080
EXPOSE 8081

ADD ./build/libs/backend-server-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
