package com.example;

import io.javalin.http.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeituraController {

    // banco de dados em memória
    private List<Leitor> bancoDeLeitores;

    public LeituraController() {
        this.bancoDeLeitores = new ArrayList<>();
        
        Leitor leitorExemplo = new Leitor("Ana", "ana@email.com");
        Livro livroExemplo = new Livro("O Guia do Mochileiro das Galáxias", "Douglas Adams", 208);
        leitorExemplo.adicionarLivro(livroExemplo);
        
        this.bancoDeLeitores.add(leitorExemplo);
    }


    public void getBoasVindas(Context ctx) {
        ctx.result("Bem-vindo ao Reading Tracker API! Use as rotas /leitores ou /leitor/{email}");
    }

    public void getTodosLeitores(Context ctx) {
        ctx.json(this.bancoDeLeitores);
    }

    public void getLeitorPorEmail(Context ctx) {
        String emailParam = ctx.pathParam("email");

        Optional<Leitor> leitorEncontrado = this.bancoDeLeitores.stream()
                .filter(leitor -> leitor.getEmail().equalsIgnoreCase(emailParam))
                .findFirst();

        if (leitorEncontrado.isPresent()) {
            ctx.json(leitorEncontrado.get()); 
        } else {
            ctx.status(404).result("Leitor não encontrado com o email: " + emailParam);
        }
    }

    public void criarLeitor(Context ctx) {
        String nome = ctx.queryParam("nome");
        String email = ctx.queryParam("email");

        if (nome == null || email == null) {
            ctx.status(400).result("Erro: 'nome' e 'email' são obrigatórios na query.");
            return;
        }

        Leitor novoLeitor = new Leitor(nome, email);

        this.bancoDeLeitores.add(novoLeitor);

        ctx.status(201).json(novoLeitor);
    }
}