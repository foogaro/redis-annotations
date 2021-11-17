FROM maven:3.8.2-jdk-8 as build
WORKDIR /build
COPY . .
RUN mvn -e clean install

FROM openjdk:8-jdk-alpine as release
WORKDIR /release
COPY --from=build /build/target/redis-spring-jpa-hibernate*.jar /release/app.jar
ENTRYPOINT ["java", "-jar"]
CMD ["/release/app.jar"]