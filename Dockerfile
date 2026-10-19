# Compila los 4 modulos con JDK 17 (imagenes oficiales eclipse-temurin).
# Spring Boot 2.4.5 trae Lombok 1.18.20, que no soporta JDK 17+, asi que se fuerza
# una version mas nueva de Lombok solo en este build (-Dlombok.version), sin tocar los pom.xml.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY cqrs-es/ cqrs-es/
COPY bank-account/ bank-account/
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f bank-account/pom.xml -B -DskipTests -Dlombok.version=1.18.34 package

FROM eclipse-temurin:17-jre AS cmd
WORKDIR /app
COPY --from=build /src/bank-account/account.cmd/target/account.cmd-1.0.0.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

FROM eclipse-temurin:17-jre AS query
WORKDIR /app
COPY --from=build /src/bank-account/account.query/target/account.query-1.0.0.jar app.jar
EXPOSE 5001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
