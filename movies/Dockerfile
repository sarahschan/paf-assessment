FROM eclipse-temurin:23-noble AS builder


ARG APP_DIR=/app
WORKDIR ${APP_DIR}

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src
COPY /data /data


RUN chmod a+x ./mvnw && ./mvnw clean package -Dmaven.test.skip=true



## Stage 2 ##
FROM eclipse-temurin:23-jre-noble

ARG DEPLOY_DIR=/app
WORKDIR ${DEPLOY_DIR}

COPY --from=builder /app/target/movies-0.0.1-SNAPSHOT.jar movies.jar
COPY --from=builder /data /data

ENV SERVER_PORT=8080
EXPOSE ${SERVER_PORT}

ENTRYPOINT java -jar movies.jar