# 1. 실행 환경 (가볍고 보안성이 좋은 Alpine 리눅스 기반)
FROM eclipse-temurin:17-jdk-alpine

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. [핵심] GitHub Actions에서 빌드된 JAR 파일만 가져옴
# (빌드를 Docker 안에서 다시 하지 않음 -> dev-secret.yml 누락 방지)
COPY build/libs/*.jar app.jar

# 4. 서버 실행
ENTRYPOINT ["java", "-jar", "app.jar"]