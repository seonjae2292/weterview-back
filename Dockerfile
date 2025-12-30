# 1. Base Image
FROM eclipse-temurin:17-jdk-alpine

# 2. Workdir
WORKDIR /app

# 3. [수정] 껍데기(plain) JAR가 덮어쓰는 사고를 방지하기 위해 구체적인 패턴 사용
# 'SNAPSHOT.jar'로 끝나는 파일만 복사 (plain.jar는 -plain.jar로 끝나서 제외됨)
COPY build/libs/*SNAPSHOT.jar app.jar

# 4. Run
ENTRYPOINT ["java", "-jar", "app.jar"]