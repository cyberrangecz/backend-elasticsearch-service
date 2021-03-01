FROM maven:3.6.2-jdk-11-slim AS build

## default environment variables for database settings
ARG PROJECT_ARTIFACT_ID=kypo-elasticsearch-service

## default link to proprietary repository, e.g., Nexus repository
ARG PROPRIETARY_REPO_URL=YOUR-PATH-TO-PROPRIETARY_REPO

# install
RUN apt-get update && apt-get install -y rsyslog netcat

# copy only essential parts
COPY /etc/kypo-elasticsearch-service.properties /app/etc/kypo-elasticsearch-service.properties
COPY entrypoint.sh /app/entrypoint.sh
COPY pom.xml /app/pom.xml
COPY /src /app/src

WORKDIR /app

# build elasticsearch service
RUN mvn clean install -DskipTests -Dproprietary-repo-url=$PROPRIETARY_REPO_URL && \
    cp /app/target/$PROJECT_ARTIFACT_ID-*.jar /app/kypo-elasticsearch-service.jar  && \
    chmod a+x entrypoint.sh

EXPOSE 8085
ENTRYPOINT ["./entrypoint.sh"]
