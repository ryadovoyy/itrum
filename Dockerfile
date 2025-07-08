FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /source
COPY gradlew .
COPY gradle gradle/
COPY settings.gradle .
COPY build.gradle .
COPY src src/
RUN chmod u+x gradlew
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --build-cache --parallel --no-daemon bootJar

FROM eclipse-temurin:17-jre-alpine
USER guest
WORKDIR /app
COPY --chown=guest --from=build /source/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
