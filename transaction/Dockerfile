FROM amazoncorretto:11
RUN set -x \
    mkdir -p /app
COPY ./build/libs/transaction-0.1-all.jar /app/
EXPOSE 8081 8083
WORKDIR /app
CMD ["java", "-jar", "transaction-0.1-all.jar"]