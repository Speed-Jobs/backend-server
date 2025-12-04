FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

EXPOSE 8080
EXPOSE 8081

# 빌드 stage에서 생성된 JAR 파일 복사
COPY --from=builder /app/build/libs/backend-server-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]