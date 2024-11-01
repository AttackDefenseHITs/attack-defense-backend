FROM gradle:7.5.1-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build -x test --no-daemon

FROM openjdk:17-jdk-slim
COPY --from=build /home/gradle/project/build/libs/*.jar /opt/service.jar
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://database:5430/attackdefencedb
ENV POSTGRES_USER=attackdefencedb
ENV POSTGRES_PASSWORD=attackdefencedb
EXPOSE 8080
CMD ["java", "-jar", "/opt/service.jar"]
