FROM eclipse-temurin:17-jdk AS build
WORKDIR /src
COPY . .
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre
# Herkunft des Standes, aus dem dieses Abbild gebaut wurde. Ohne Angabe meldet der
# Dienst "unbekannt" statt einer Behauptung.
ARG BUILD_REVISION=unbekannt
ARG BUILD_REF=unbekannt
ENV STROMENTLASTUNG_BUILD_REVISION=$BUILD_REVISION \
    STROMENTLASTUNG_BUILD_REF=$BUILD_REF \
    STROMENTLASTUNG_DATA_DIR=/app/data
WORKDIR /app
COPY --from=build /src/target/*.jar /app/app.jar
EXPOSE 8094
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
