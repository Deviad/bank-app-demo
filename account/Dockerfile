FROM amazoncorretto:11
RUN set -x \
    mkdir -p /app
COPY ./build/libs/account-0.1-all.jar /app/
EXPOSE 8080 8082
WORKDIR /app
CMD ["java", "-jar", "account-0.1-all.jar"]