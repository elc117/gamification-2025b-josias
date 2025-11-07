# Java Project

Este é um projeto Java configurado para ser executado em um contêiner de desenvolvimento. Abaixo estão as informações sobre a estrutura do projeto e como utilizá-lo.

## Estrutura do Projeto

```
java-project
├── .devcontainer
│   ├── devcontainer.json
│   └── Dockerfile
├── src
│   ├── main
│   │   └── java
│   │       └── com
│   │           └── example
│   │               └── App.java
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── AppTest.java
├── .gitignore
├── pom.xml
└── README.md
```

## Pré-requisitos

- Java JDK 11 ou superior
- Maven
- Docker (para o ambiente de contêiner)

## Instalação

1. Clone o repositório:
   ```
   git clone <url-do-repositorio>
   cd java-project
   ```

2. Abra o projeto em um editor de código compatível com contêineres, como o VS Code.

3. O ambiente de desenvolvimento será configurado automaticamente com base no arquivo `.devcontainer/devcontainer.json`.

## Uso

Para compilar e executar o projeto, utilize os seguintes comandos:

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.example.App"
```

## Testes

Os testes podem ser executados com o Maven:

```bash
mvn test
```

## Contribuição

Sinta-se à vontade para contribuir com melhorias ou correções. Faça um fork do repositório, crie uma branch para suas alterações e envie um pull request.

## Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).