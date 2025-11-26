package io.literato.controller;

import io.javalin.http.Context;
import io.literato.model.ItemEstante;
import io.literato.service.GamificacaoService;
import java.util.List;

public class EstanteController {

    private final GamificacaoService service;

    public EstanteController(GamificacaoService service) {
        this.service = service;
    }

    public void listar(Context ctx) {
        int usuarioId = Integer.parseInt(ctx.pathParam("usuarioId"));
        List<ItemEstante> estante = service.listarEstante(usuarioId);
        ctx.json(estante);
    }

    public void iniciarLeitura(Context ctx) {
        try {
            ItemEstante item = ctx.bodyAsClass(ItemEstante.class);
            service.iniciarLeitura(item);
            ctx.status(201).result("Livro adicionado à estante!");
        } catch (Exception e) {
            ctx.status(400).result("Erro ao adicionar livro: " + e.getMessage());
        }
    }

    public void atualizarProgresso(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            String paginasParam = ctx.queryParam("paginas");
            if (paginasParam == null) throw new IllegalArgumentException("Parâmetro 'paginas' obrigatório");
            
            int paginas = Integer.parseInt(paginasParam);
            
            service.registrarProgresso(id, paginas);
            ctx.status(200).result("Progresso atualizado!");
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Erro interno: " + e.getMessage());
        }
    }

    public void editarProgresso(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            String paginasParam = ctx.queryParam("paginas");
            if (paginasParam == null) throw new IllegalArgumentException("Parâmetro 'paginas' obrigatório");
            
            int paginas = Integer.parseInt(paginasParam);
            
            service.editarProgressoAbsoluto(id, paginas);
            ctx.status(200).result("Progresso editado!");
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Erro: " + e.getMessage());
        }
    }

    public void atualizarStatus(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            String status = ctx.queryParam("status");
            if (status == null) throw new IllegalArgumentException("Parâmetro 'status' obrigatório");
            
            service.atualizarStatusEstante(id, status);
            ctx.status(200).result("Status atualizado!");
        } catch (Exception e) {
            ctx.status(500).result("Erro: " + e.getMessage());
        }
    }

    public void remover(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            service.removerDaEstante(id);
            ctx.status(204); 
        } catch (Exception e) {
            ctx.status(500).result("Erro: " + e.getMessage());
        }
    }

    private static class ProgressoDTO {
        public int paginas;
    }

    private static class StatusDTO {
        public String status;
    }
}
