#FROM adoptopenjdk/openjdk11:latest AS build
FROM openjdk:11 AS build

ARG GH_USER
ARG GH_TOKEN

RUN mkdir /src
COPY . /src
WORKDIR /src
RUN ./gradlew shadowJar --no-daemon

#FROM alpine:3.14
FROM adoptopenjdk/openjdk11:alpine

RUN mkdir /app
COPY --from=build /src/build/libs/*.jar /app/application.jar

CMD ["java", "-jar", "/app/application.jar"]