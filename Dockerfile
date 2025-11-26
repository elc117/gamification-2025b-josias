# 1. Imagem Base: Começa com uma imagem oficial que já tem Maven e Java 17.
FROM maven:3-eclipse-temurin-17

# 2. Diretório de Trabalho: Cria e entra na pasta /app dentro do contêiner.
WORKDIR /app

# 3. Copia o seu projeto: Copia tudo da sua pasta (pom.xml, src, etc.) para o /app
#    (O .dockerignore vai pular pastas desnecessárias como 'target')
COPY . .

# 4. Pré-Build: Baixa as dependências e compila o projeto.
#    Isso agiliza o processo de desenvolvimento.
RUN mvn clean install

# 5. Comando Padrão: Mantém o contêiner rodando "para sempre"
#    para que o VS Code possa se conectar a ele.
CMD ["sleep", "infinity"]