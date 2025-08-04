## 배포 준비를 위한 Dockerfile을 작성합니다.(현재는 작동 X)
## 1. 빌드(컴파일)를 위한 단계
#FROM openjdk:21-jdk-slim as builder
#WORKDIR /app
#COPY gradlew .
#COPY gradle gradle
#COPY build.gradle .
#COPY settings.gradle .
#RUN ./gradlew bootJar --no-daemon
#
## 2. 실제 실행을 위한 최종 단계
#FROM openjdk:21-jre-slim
#WORKDIR /app
## 빌드 단계에서 만들어진 JAR 파일만 복사
#COPY --from=builder /app/build/libs/*.jar app.jar
## 8080 포트로 서비스 실행
#EXPOSE 8080
#CMD ["java", "-jar", "app.jar"]