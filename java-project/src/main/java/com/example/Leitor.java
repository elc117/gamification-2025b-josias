package com.example;

import java.util.ArrayList;
import java.util.List;

public class Leitor {

    private String nome;
    private String email;
    private List<Livro> estanteDeLivros;

    public Leitor(String nome, String email) {
        this.nome = nome;
        this.email = email;
        
        this.estanteDeLivros = new ArrayList<>();
        
        System.out.println("Novo leitor criado: " + this.nome);
    }


    public void adicionarLivro(Livro livro) {
        this.estanteDeLivros.add(livro);
        System.out.println("O livro '" + livro.getTitulo() + "' foi adicionado à estante de " + this.nome);
    }

    public String getNome() {
        return this.nome;
    }
    
    public List<Livro> getEstanteDeLivros() {
        return this.estanteDeLivros;
    }
    
    public String getEmail() {
        return this.email;

    }
}