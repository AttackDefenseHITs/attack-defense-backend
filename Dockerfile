FROM gradle:7.5.1-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build -x test --no-daemon

FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y python3 python3-pip
COPY --from=build /home/gradle/project/build/libs/*.jar /opt/service.jar
EXPOSE 8080
CMD ["java", "-jar", "/opt/service.jar"]
