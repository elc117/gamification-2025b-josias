# Literato - Sistema de Leitura Gamificado

TODO 

## Identificação
**Nome:** Josias Augusto Kautzmann
**Curso:** Sistemas de Informação
**Disciplina:** Paradigmas de Programação

---

## Proposta
O **Literato** é um sistema de backend desenvolvido em Java para gamificar o hábito da leitura. O objetivo é permitir que usuários registrem seu progresso de leitura, mantenham uma sequência de dias lendo (streak) e desbloqueiem conquistas baseadas em seu desempenho.


---

## Processo de Desenvolvimento

> *todo*

### Recursos de Orientação a Objetos Utilizados
*(Escreva aqui com suas palavras como você usou os itens abaixo. A professora pediu para não usar IA neste texto pessoal, mas aqui estão os pontos técnicos que implementamos):*
*   **Encapsulamento:** Usado nas classes de Modelo (`Usuario`, `Leitura`) para proteger os dados com getters e setters.
*   **Padrão DAO (Data Access Object):** Separação completa entre a lógica de negócio e o acesso ao banco de dados SQLite.
*   **Injeção de Dependência:** O `GamificacaoService` recebe os DAOs no construtor, facilitando testes e manutenção.
*   **Polimorfismo:** Implementado no sistema de Conquistas. A interface `Conquista` e a classe abstrata `ConquistaBase` permitem que o serviço trate diferentes tipos de conquistas (`ConquistaPaginas`, `ConquistaStreak`, etc.) de forma genérica, facilitando a criação de novas regras sem alterar o código principal.

### Dificuldades e Soluções
*cors e data para streak e ranking*.


---

Pré-requisitos
Java 17 ou superior
Maven
(Opcional) VS Code com extensão "Dev Containers" para ambiente automático.
Instalação e Execução
Clone o repositório.
Na raiz do projeto, compile o código: mvn clean compile
Execute a aplicação: mvn exec:java -Dexec.mainClass="io.literato.Main"
O servidor iniciará na porta 7070.
Testando a API
Você pode usar o arquivo requests.http incluído no projeto (se usar VS Code) ou comandos curl.

Exemplo - Registrar Leitura:

Exemplo - Ver Ranking:

Referências e Créditos

Framework Web: Javalin
Banco de Dados: SQLite (via JDBC)
Assistência Técnica: GitHub Copilot (Apoio na geração de boilerplate e configuração do Maven).