# 1. Estágio de Build
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# Compila o projeto e gera o JAR (pula testes para agilizar)
RUN mvn clean package -DskipTests

# 2. Estágio de Execução (Imagem mais leve apenas com JRE)
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/literato-api-1.0-SNAPSHOT.jar app.jar

# Define a porta (opcional, mas boa prática)
ENV PORT=7070
EXPOSE 7070

# Comando para iniciar a aplicação
CMD ["java", "-jar", "app.jar"]