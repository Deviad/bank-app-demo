FROM amazoncorretto:11 as builder
RUN set -x \
    mkdir -p /compile
COPY ./ /compile
WORKDIR /compile
RUN ./gradlew clean build

FROM amazoncorretto:11
RUN set -x \
    mkdir -p /app
COPY --from=builder /compile/build/libs/account-0.1-all-optimized.jar /app/
EXPOSE 8080 8082
WORKDIR /app
CMD ["java", "-jar", "account-0.1-all-optimized.jar"]