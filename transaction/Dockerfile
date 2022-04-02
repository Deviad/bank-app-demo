FROM amazoncorretto:11 as builder
RUN set -x \
    mkdir -p /compile
COPY ./ /compile
WORKDIR /compile
RUN ./gradlew clean build -x test

FROM amazoncorretto:11
RUN set -x \
    mkdir -p /app
COPY --from=builder /compile/build/libs/transaction-0.1-all-optimized.jar /app/
EXPOSE 8081 8083
WORKDIR /app
CMD ["java", "-jar", "transaction-0.1-all-optimized.jar"]