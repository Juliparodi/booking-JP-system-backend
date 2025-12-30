# Use Eclipse Temurin JDK 21
FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

COPY ${JAR_FILE} app.jar

# Run as non-root
USER app

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
