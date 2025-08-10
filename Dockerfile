FROM eclipse-temurin:17-jre-alpine

EXPOSE 8301

# Create logs directory with full permissions
RUN mkdir -p /logs && chmod -R 755 /logs

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bgibridge.jar

ENTRYPOINT ["java","-jar","/bgibridge.jar"]