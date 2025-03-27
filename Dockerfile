FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src/
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY wait-for-it.sh /app/
RUN chmod +x /app/wait-for-it.sh

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "./wait-for-it.sh mariadb:3306 --timeout=30 --strict -- java -jar app.jar"]