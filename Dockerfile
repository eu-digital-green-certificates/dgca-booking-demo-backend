FROM adoptopenjdk:11-jre-hotspot as build
COPY ./target/*.jar /app/app.jar
WORKDIR /app

FROM nginx:alpine
COPY --from=build ./app /app
EXPOSE 80
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar ./app/app.jar" ]
