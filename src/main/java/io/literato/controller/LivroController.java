package io.literato.controller;

import io.javalin.http.Context;
import io.literato.dao.LivroDAO;
import io.literato.model.Livro;
import java.util.List;

public class LivroController {

    private final LivroDAO livroDAO;

    public LivroController() {
        this.livroDAO = new LivroDAO();
    }

    public void buscar(Context ctx) {
        String query = ctx.queryParam("q");
        if (query == null || query.trim().isEmpty()) {
            ctx.json(List.of());
            return;
        }

        List<Livro> livros = livroDAO.buscarPorTitulo(query);
        ctx.json(livros);
    }
}
