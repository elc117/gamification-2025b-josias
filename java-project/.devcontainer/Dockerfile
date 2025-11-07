FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /workspace

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && mvn dependency:go-offline

# Copy the source code into the container
COPY src ./src

# Comando para manter o container "vivo" para o VS Code se conectar
# (O comando antigo 'spring-boot:run' estava incorreto para este projeto)
CMD ["sleep", "infinity"]