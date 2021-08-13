FROM openjdk:11-jdk as builder

COPY Stdlib/ /tmp/Stdlib/
COPY DSL/ /tmp/DSL/

WORKDIR /tmp/DSL/

RUN ./gradlew war


FROM tomcat:10-jdk11 as runner

COPY --from=builder \
    /tmp/DSL/de.unibonn.simpleml.web/build/libs/de.unibonn.simpleml.web-1.0.0-SNAPSHOT.war \
    /usr/local/tomcat/webapps/simpleml.war

EXPOSE 8080
