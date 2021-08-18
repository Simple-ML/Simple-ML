FROM gradle:7-jdk11 as builder

COPY . /tmp/

WORKDIR /tmp/

RUN gradle war


# Tomcat 10 expects Jakarta servlets rather than JavaEE HttpServlets and, thus, does not work
FROM tomcat:9-jdk11 as runner

COPY --from=builder \
    /tmp/de.unibonn.simpleml.web/build/libs/de.unibonn.simpleml.web-1.0.0-SNAPSHOT.war \
    /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
