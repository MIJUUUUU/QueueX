FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
COPY --from=build /app/target/queuex-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
CMD ["java", "-jar", "app.jar"]
