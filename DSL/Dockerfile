FROM gradle:7-jdk11 as cache

RUN mkdir -p /home/gradle/cache_home

ENV GRADLE_USER_HOME /home/gradle/cache_home

COPY buildSrc/ /home/gradle/java-code/buildSrc/
COPY build.gradle.kts gradle.properties settings.gradle.kts /home/gradle/java-code/
COPY de.unibonn.simpleml/build.gradle.kts /home/gradle/java-code/de.unibonn.simpleml/
COPY de.unibonn.simpleml.ide/build.gradle.kts /home/gradle/java-code/de.unibonn.simpleml.ide/
COPY de.unibonn.simpleml.web/build.gradle.kts /home/gradle/java-code/de.unibonn.simpleml.web/

WORKDIR /home/gradle/java-code/

RUN gradle dependencies -i --stacktrace

FROM gradle:7-jdk11 as builder

COPY --from=cache /home/gradle/cache_home/ /home/gradle/.gradle/
COPY . /app/

WORKDIR /app/

RUN gradle war -i --stacktrace


# Tomcat 10 expects Jakarta servlets rather than JavaEE HttpServlets and, thus, does not work
FROM tomcat:10-jdk11 as runner

COPY --from=builder \
    /app/de.unibonn.simpleml.web/build/libs/de.unibonn.simpleml.web-1.0.0-SNAPSHOT.war \
    /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
