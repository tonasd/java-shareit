FROM amazoncorretto:11-alpine-jdk
LABEL authors="tonasd"
ARG JAR_FILE=target/*.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9091
COPY ${JAR_FILE} share-it-server.jar
ENTRYPOINT ["java", "-jar", "/share-it-server.jar"]