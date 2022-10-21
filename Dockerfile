FROM eclipse-temurin:17 as build

RUN mkdir -p /usr/local/src/attoly/api
WORKDIR /usr/local/src/attoly/api

COPY .mvn/ .mvn
COPY mvnw ./
COPY pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package -Dmaven.test.skip=true

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17

RUN mkdir -p /usr/local/bin/attoly/api
WORKDIR /usr/local/bin/attoly/api

COPY --from=build /usr/local/src/attoly/api/target/dependency/BOOT-INF/lib ./lib
COPY --from=build /usr/local/src/attoly/api/target/dependency/META-INF ./META-INF
COPY --from=build /usr/local/src/attoly/api/target/dependency/BOOT-INF/classes ./

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-cp", "./:./lib/*", "de.x1c1b.attoly.api.AttolyApiApplication"]
