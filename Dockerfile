FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# [수정] *.jar 대신, plain.jar가 아닌 확실한 실행 파일만 복사한다.
# 보통 빌드된 파일은 '프로젝트명-버전.jar' 형태다.
# plain이 붙지 않은 jar를 찾아서 app.jar로 복사한다.
#COPY build/libs/*SNAPSHOT.jar app.jar

# 만약 위 명령어가 불안하다면, 아래처럼 정확한 파일명을 쓰는 게 실무 정석이다.
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]