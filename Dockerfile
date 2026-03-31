# 1. 빌드 단계 (Build Stage)
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

# 2. 실행 단계 (Run Stage)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# MySQL, Redis 연결 대기를 위해 8080 포트 노출
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]