package io.literato;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.bundled.CorsPlugin;
import io.literato.controller.ClubeController;
import io.literato.controller.EstanteController;
import io.literato.controller.LeituraController;
import io.literato.controller.LivroController;
import io.literato.service.GamificacaoService;
import io.literato.dao.ClubeDAO;
import io.literato.dao.EstanteDAO;
import io.literato.dao.LeituraDAO;
import io.literato.dao.UsuarioDAO;
import io.literato.dao.Database;

public class Main {

    public static void main(String[] args) {
        
        // 1. Inicializa Banco
        Database.initialize();

        // 2. Injeção de Dependências
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        LeituraDAO leituraDAO = new LeituraDAO();
        ClubeDAO clubeDAO = new ClubeDAO();
        EstanteDAO estanteDAO = new EstanteDAO(); // Novo DAO
        
        // Service agora recebe 4 DAOs
        GamificacaoService gamificacaoService = new GamificacaoService(usuarioDAO, leituraDAO, clubeDAO, estanteDAO);
        
        LeituraController leituraController = new LeituraController(gamificacaoService);
        ClubeController clubeController = new ClubeController(gamificacaoService);
        LivroController livroController = new LivroController();
        EstanteController estanteController = new EstanteController(gamificacaoService); // Novo Controller

        // 3. Configuração do Servidor
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
            config.registerPlugin(new CorsPlugin(cors -> {
                cors.addRule(rule -> { rule.anyHost(); });
            }));
        }).start(7070); 

        System.out.println("Servidor Literato V2 rodando na porta 7070 🚀");

        // 4. Rotas da API
        
        app.get("/api/estante/{usuarioId}", estanteController::listar);
        app.post("/api/estante", estanteController::iniciarLeitura);
        app.put("/api/estante/{id}/progresso", estanteController::atualizarProgresso); // Soma
        app.put("/api/estante/{id}/editar", estanteController::editarProgresso); // Edita absoluto
        app.put("/api/estante/{id}/status", estanteController::atualizarStatus);
        app.delete("/api/estante/{id}", estanteController::remover);

        // Livros 
        app.get("/api/livros", livroController::buscar);

        // Usuários e Leituras
        app.get("/api/usuarios/{id}", leituraController::buscarUsuario);
        app.get("/api/perfil/{id}", leituraController::verPerfil); 
        app.get("/api/ranking", leituraController::listarRanking);
        app.post("/api/login", leituraController::realizarLogin);
        app.post("/api/usuarios", leituraController::criarUsuario);
        app.put("/api/usuarios/{id}", leituraController::atualizarPerfil);

        // Clubes 
        app.get("/api/clubes", clubeController::listarClubes);
        app.post("/api/clubes", clubeController::criarClube);
        app.put("/api/clubes/{id}", clubeController::atualizarClube); // Nova rota de edição
        app.post("/api/clubes/{id}/entrar", clubeController::entrarNoClube);
        app.get("/api/clubes/{id}/membros", clubeController::listarMembros);
        app.delete("/api/clubes/{id}", clubeController::excluirClube);
        app.delete("/api/clubes/{id}/sair", clubeController::sairDoClube);
        app.put("/api/clubes/{id}/membros/{usuarioId}/cargo", clubeController::alterarCargo);
        app.delete("/api/clubes/{id}/membros/{usuarioId}", clubeController::expulsarMembro); // Nova rota de expulsão

        // Clubes - Solicitações
        app.get("/api/clubes/{id}/solicitacoes", clubeController::listarSolicitacoes);
        app.post("/api/solicitacoes/{solicitacaoId}/responder", clubeController::responderSolicitacao);
    }
}