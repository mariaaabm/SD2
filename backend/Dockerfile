# Constrói a imagem do backend em duas fases para manter a imagem final pequena, a primeira compila o projeto com Maven e Java 21, a segunda copia apenas o JAR resultante para uma imagem JRE leve que arranca na porta 8080.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
