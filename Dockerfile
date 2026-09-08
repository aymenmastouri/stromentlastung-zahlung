FROM eclipse-temurin:17-jdk AS build
WORKDIR /src
COPY . .
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
ENV STROMENTLASTUNG_DATA_DIR=/app/data
EXPOSE 8094
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
