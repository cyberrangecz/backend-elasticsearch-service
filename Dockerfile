FROM maven:3.6.2-jdk-11-slim AS build

## default environment variables for database settings
ARG USERNAME=postgres
ARG PASSWORD=postgres
ARG PROJECT_ARTIFACT_ID=kypo-elasticsearch-service

## default link to proprietary repository, e.g., Nexus repository
ARG PROPRIETARY_REPO_URL=YOUR-PATH-TO-PROPRIETARY_REPO

# install
RUN apt-get update && apt-get install -y rsyslog

# copy only essential parts
COPY /etc/kypo-elasticsearch-service.properties /app/etc/kypo-elasticsearch-service.properties
COPY pom.xml /app/pom.xml
COPY /src /app/src

# build elasticsearch service
RUN cd /app && \
    mvn clean install -DskipTests -Dproprietary-repo-url=$PROPRIETARY_REPO_URL && \
    cp /app/target/$PROJECT_ARTIFACT_ID-*.jar /app/kypo-elasticsearch-service.jar

WORKDIR /app
EXPOSE 8085
ENTRYPOINT ["java", "-Dspring.config.location=/app/etc/kypo-elasticsearch-service.properties", "-jar",  "/app/kypo-elasticsearch-service.jar"]
