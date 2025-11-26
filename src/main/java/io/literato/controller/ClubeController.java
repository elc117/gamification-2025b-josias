package io.literato.controller;

import io.javalin.http.Context;
import io.literato.model.Clube;
import io.literato.service.GamificacaoService;
import java.sql.SQLException;

public class ClubeController {

    private final GamificacaoService service;

    public ClubeController(GamificacaoService service) {
        this.service = service;
    }

    public void listarClubes(Context ctx) {
        ctx.json(service.listarClubes());
    }

    public void criarClube(Context ctx) {
        try {
            Clube novoClube = ctx.bodyAsClass(Clube.class);
            service.criarClube(novoClube);
            ctx.status(201).result("Clube criado com sucesso!");
        } catch (SQLException e) {
            ctx.status(500).result("Erro ao criar clube: " + e.getMessage());
        }
    }

    public void entrarNoClube(Context ctx) {
        try {
            int clubeId = Integer.parseInt(ctx.pathParam("id"));
            String uidParam = ctx.queryParam("usuarioId");
            
            if (uidParam == null) {
                 ctx.status(400).result("usuarioId é obrigatório");
                 return;
            }
            
            int usuarioId = Integer.parseInt(uidParam);
            service.entrarNoClube(usuarioId, clubeId);
            ctx.status(200).result("Você entrou no clube!");
        } catch (IllegalStateException e) {
            // Solicitação enviada (Clube Privado)
            ctx.status(202).result(e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Erro ao entrar no clube");
        }
    }
    
    public void listarMembros(Context ctx) {
        int clubeId = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(service.listarMembrosClubeComCargo(clubeId));
    }

    public void listarSolicitacoes(Context ctx) {
        int clubeId = Integer.parseInt(ctx.pathParam("id"));
        int usuarioId = Integer.parseInt(ctx.queryParam("usuarioId")); // quem está pedindo a lista deve ser o dono
        try {
            ctx.json(service.listarSolicitacoesClube(clubeId, usuarioId));
        } catch (SecurityException e) {
            ctx.status(403).result(e.getMessage());
        }
    }

    public void responderSolicitacao(Context ctx) {
        int solicitacaoId = Integer.parseInt(ctx.pathParam("solicitacaoId"));
        boolean aceitar = Boolean.parseBoolean(ctx.queryParam("aceitar"));
        int usuarioId = Integer.parseInt(ctx.queryParam("usuarioId")); // Quem está respondendo (dono)

        System.out.println("Responder Solicitacao: ID=" + solicitacaoId + " Aceitar=" + aceitar + " Dono=" + usuarioId);

        try {
            service.responderSolicitacaoClube(solicitacaoId, aceitar, usuarioId);
            ctx.status(200).result("Solicitação " + (aceitar ? "aceita" : "rejeitada"));
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Solicitação não encontrada")) {
                ctx.status(404).result("Solicitação não encontrada.");
            } else {
                ctx.status(500).result("Erro ao processar solicitação: " + e.getMessage());
            }
        }
    }

    public void excluirClube(Context ctx) {
        int clubeId = Integer.parseInt(ctx.pathParam("id"));
        int usuarioId = Integer.parseInt(ctx.queryParam("usuarioId"));
        try {
            service.excluirClube(clubeId, usuarioId);
            ctx.status(204);
        } catch (SecurityException e) {
            ctx.status(403).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Erro ao excluir clube: " + e.getMessage());
        }
    }

    public void sairDoClube(Context ctx) {
        int clubeId = Integer.parseInt(ctx.pathParam("id"));
        int usuarioId = Integer.parseInt(ctx.queryParam("usuarioId"));
        try {
            service.sairDoClube(clubeId, usuarioId);
            ctx.status(204);
        } catch (IllegalStateException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Erro ao sair do clube: " + e.getMessage());
        }
    }

    public void atualizarClube(Context ctx) {
        int clubeId = Integer.parseInt(ctx.pathParam("id"));
        int usuarioId = Integer.parseInt(ctx.queryParam("usuarioId"));
        try {
            Clube dados = ctx.bodyAsClass(Clube.class);
            dados.setId(clubeId); // Garante ID correto
            service.atualizarClube(clubeId, dados, usuarioId);
            ctx.status(200).result("Clube atualizado!");
        } catch (SecurityException e) {
            ctx.status(403).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Erro ao atualizar clube: " + e.getMessage());
        }
    }

    public void expulsarMembro(Context ctx) {
        int clubeId = Integer.parseInt(ctx.pathParam("id"));
        int usuarioExpulsoId = Integer.parseInt(ctx.pathParam("usuarioId"));
        int usuarioLogadoId = Integer.parseInt(ctx.queryParam("usuarioLogadoId"));

        try {
            service.expulsarMembro(clubeId, usuarioExpulsoId, usuarioLogadoId);
            ctx.status(204);
        } catch (SecurityException e) {
            ctx.status(403).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Erro ao expulsar membro: " + e.getMessage());
        }
    }

    public void alterarCargo(Context ctx) {
        int clubeId = Integer.parseInt(ctx.pathParam("id"));
        int usuarioAlvoId = Integer.parseInt(ctx.pathParam("usuarioId"));
        int usuarioLogadoId = Integer.parseInt(ctx.queryParam("usuarioLogadoId"));
        String novoCargo = ctx.queryParam("cargo"); // MEMBRO, MODERADOR, DONO

        try {
            service.alterarCargoMembro(clubeId, usuarioAlvoId, novoCargo, usuarioLogadoId);
            ctx.status(200).result("Cargo atualizado");
        } catch (SecurityException e) {
            ctx.status(403).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Erro ao alterar cargo: " + e.getMessage());
        }
    }
}