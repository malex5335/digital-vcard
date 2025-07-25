#
# Frontend stage
#
FROM node:20-alpine as frontend
ENV LOCAL=src/main/ui
WORKDIR /app
COPY $LOCAL/package*.json ./
RUN npm install
COPY $LOCAL/ .
RUN npm run build

#
# Build stage
#
FROM maven:3-eclipse-temurin-24-alpine AS build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
COPY --from=frontend /app/dist/ src/main/resources/static/
RUN mvn clean install

#
# Run stage
#
FROM eclipse-temurin:24-alpine AS run
ARG JAR_FILE=/usr/app/target/*.jar
COPY --from=build $JAR_FILE /app/runner.jar
EXPOSE 8080
ENTRYPOINT exec java -jar /app/runner.jar