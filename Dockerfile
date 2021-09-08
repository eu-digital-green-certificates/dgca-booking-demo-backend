FROM adoptopenjdk:11-jre-hotspot as build
COPY ./target/*.jar /app/app.jar
WORKDIR /app

FROM nginx:alpine
COPY --from=build ./app /app
COPY nginx/default.conf.template /etc/nginx/conf.d/default.conf
RUN apk --no-cache add openjdk11-jre 

EXPOSE 80
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar ./app/app.jar" ]
ENTRYPOINT [ "ngnix" ]
