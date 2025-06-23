FROM maven:3.9.8-amazoncorretto-21 AS build
WORKDIR /opt/app
COPY mvnw pom.xml ./
COPY ./src ./src
RUN mvn clean install -DskipTests

FROM amazoncorretto:21-alpine-jdk
#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8091
WORKDIR /opt/app
#COPY target/*.jar app.jar
COPY --from=build /opt/app/target/*.jar /opt/app/*.jar
#COPY --from=build /target/*.jar *.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/opt/app/*.jar"]
#ENTRYPOINT ["java","-jar","/*.jar"]