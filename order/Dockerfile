FROM gradle:7.6.4-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src/producer
WORKDIR /home/gradle/src/producer
RUN gradle bootJar --no-daemon --stacktrace
FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY --from=build /home/gradle/src/producer/build/libs/*.jar order.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/order.jar"]