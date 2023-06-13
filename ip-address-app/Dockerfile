# Autor: Bartłomiej Sulima
FROM maven:3-openjdk-17-slim AS build
WORKDIR /usr/local/build

# wykorzystanie cache: pobranie zależności przed zbudowaniem aplikacji
# tylko zmiany w pom.xml powinny powodować ponowienie tych kroków
COPY pom.xml .
RUN mvn verify -B --fail-never

# zbudowanie aplikacji
COPY . .
RUN mvn clean install

FROM openjdk:17-jdk-slim
# openjdk:17-jdk-slim jest wykorzystywany przez maven:3-openjdk-17-slim co zmniejsza ilość warstw
COPY --from=build /usr/local/build/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]