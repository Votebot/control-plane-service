FROM gradle AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM adoptopenjdk:8-jre-openj9
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/control-plane.jar
CMD ["java", "-jar", "/app/control-plane.jar"]