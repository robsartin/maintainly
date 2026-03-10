FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B
COPY src src
COPY config config
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/mystuff-0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
