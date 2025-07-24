# 1단계: 빌드 환경 설정 (멀티 스테이지 빌드 사용)
# OpenJDK 17 기반의 JRE 이미지를 사용합니다.
FROM eclipse-temurin:17-jre-jammy as builder

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 실제 실행 가능한 JAR 파일을 복사합니다.
# build.gradle에 따라 빌드된 JAR 파일의 정확한 경로를 지정해야 합니다.
# 일반적으로 build/libs/프로젝트명-버전.jar 형태로 생성됩니다.
ARG JAR_FILE=interview-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 2단계: 최종 이미지 생성
# 가볍고 최적화된 JRE 이미지를 사용합니다.
FROM eclipse-temurin:17-jre-jammy

# 환경 변수 설정 (옵션)
# 예를 들어, Spring Profile을 지정할 수 있습니다.
# ENV SPRING_PROFILES_ACTIVE=prod

# 빌드 스테이지에서 생성된 JAR 파일을 복사합니다.
COPY --from=builder /app/app.jar /app/app.jar

# 애플리케이션이 사용할 포트를 노출합니다.
# Spring Boot의 기본 포트는 8080입니다.
EXPOSE 8080

# 컨테이너 시작 시 실행될 명령어를 정의합니다.
# java -jar 명령어로 스프링 부트 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]