FROM openjdk:11-jdk as builder

COPY /server/de.unibonn.simpleml.parent /tmp/de.unibonn.simpleml.parent

RUN cd /tmp/de.unibonn.simpleml.parent && ./gradlew war


FROM tomcat:10-jdk11

COPY --from=builder \
    /tmp/de.unibonn.simpleml.parent/de.unibonn.simpleml.web/build/libs/de.unibonn.simpleml.web-1.0.0-SNAPSHOT.war \
    /usr/local/tomcat/webapps/simpleml.war

EXPOSE 8080
