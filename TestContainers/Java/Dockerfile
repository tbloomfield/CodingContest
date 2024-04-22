FROM alpine/java:22-jdk
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} java-testcontainer.jar
ENTRYPOINT ["java","-jar","/java-testcontainer.jar"]