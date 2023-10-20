FROM eclipse-temurin:8-jdk-alpine

RUN mkdir /app \
  && adduser -D jminix \
  && chown -R jminix /app

WORKDIR /app

USER jminix

COPY target/jminix.jar /app/jminix.jar

EXPOSE 8181/tcp

ENTRYPOINT ["java"]
CMD ["-jar","jminix.jar"]
