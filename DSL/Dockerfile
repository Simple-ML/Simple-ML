FROM openjdk:11-jdk as builder

COPY . /tmp/

RUN cd /tmp/ && ./gradlew war


FROM tomcat:10-jdk11

COPY --from=builder \
    /tmp/de.unibonn.simpleml.web/build/libs/de.unibonn.simpleml.web-1.0.0-SNAPSHOT.war \
    /usr/local/tomcat/webapps/simpleml.war

EXPOSE 8080
