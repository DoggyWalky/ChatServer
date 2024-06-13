# OpenJDK 17 버전의 환경을 구성
FROM eclipse-temurin:17

# 빌드 시 환경 변수를 설정하기 위해 ARG 사용
ARG JAR_FILE=build/libs/*.jar

# JAR_FILE을 app.jar로 복사
COPY ${JAR_FILE} /app2/app2.jar

# 런타임에 사용할 환경 변수를 설정하기 위해 ENV 사용
ARG JASYPT_PASSWORD
ENV JASYPT_PASSWORD_ENV=${JASYPT_PASSWORD}

# 환경 변수를 사용하여 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java -jar -Djasypt.encryptor.password=${JASYPT_PASSWORD_ENV} -Dcom.amazonaws.sdk.disableEc2Metadata=true /app2/app2.jar"]