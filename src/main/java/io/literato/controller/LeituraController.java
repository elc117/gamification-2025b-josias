package io.literato.controller;

import io.javalin.http.Context;
import io.literato.model.Leitura;
import io.literato.model.Usuario; // Importação essencial que estava faltando
import io.literato.service.GamificacaoService;
import io.literato.model.PerfilDTO;
import java.util.List;
import java.sql.SQLException;

public class LeituraController {

    private final GamificacaoService service;

    public LeituraController(GamificacaoService service) {
        this.service = service;
    }

    public void buscarUsuario(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Usuario usuario = service.buscarStatusUsuario(id);
        
        if (usuario != null) {
            ctx.json(usuario);
        } else {
            ctx.status(404).result("Usuário não encontrado");
        }
    }

    public void listarRanking(Context ctx) {
        System.out.println("CONTROLLER: Recebida requisição de ranking");
        String clubeIdParam = ctx.queryParam("clubeId");
        Integer clubeId = (clubeIdParam != null && !clubeIdParam.isEmpty()) ? Integer.parseInt(clubeIdParam) : null;
        
        String periodo = ctx.queryParam("periodo");
        
        List<Usuario> ranking = service.obterRanking(clubeId, periodo);
        ctx.json(ranking);
    }

    public void realizarLogin(Context ctx) {
        try {
            Usuario credenciais = ctx.bodyAsClass(Usuario.class);
            Usuario usuarioLogado = service.login(credenciais.getNome(), credenciais.getSenha());
            
            if (usuarioLogado != null) {
                ctx.json(usuarioLogado);
            } else {
                ctx.status(401).result("Usuário ou senha inválidos");
            }
        } catch (Exception e) {
            ctx.status(400).result("Erro no login: " + e.getMessage());
        }
    }

    public void criarUsuario(Context ctx) {
        try {
            Usuario novoUsuario = ctx.bodyAsClass(Usuario.class);
            if (novoUsuario.getNome() == null || novoUsuario.getSenha() == null) {
                ctx.status(400).result("Nome e senha são obrigatórios");
                return;
            }
            service.criarUsuario(novoUsuario);
            ctx.status(201).result("Usuário criado com sucesso");
        } catch (SQLException e) {
            ctx.status(500).result("Erro ao criar usuário: " + e.getMessage());
        }
    }

    public void atualizarPerfil(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario dados = ctx.bodyAsClass(Usuario.class);
            
            service.atualizarPerfil(id, dados.getBio(), dados.getFoto());
            ctx.status(200).result("Perfil atualizado com sucesso");
        } catch (SQLException e) {
            ctx.status(500).result("Erro ao atualizar perfil");
        }
    }

    public void verPerfil(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            PerfilDTO perfil = service.obterPerfilCompleto(id);
            if (perfil != null) {
                ctx.json(perfil);
            } else {
                ctx.status(404).result("Usuário não encontrado");
            }
        } catch (Exception e) {
            ctx.status(500).result("Erro ao carregar perfil: " + e.getMessage());
        }
    }
}