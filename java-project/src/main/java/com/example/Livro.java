package com.example;

public class Livro {

    private String titulo;
    private String autor;
    private int totalPaginas;

    public Livro(String titulo, String autor, int totalPaginas) {
        this.titulo = titulo;
        this.autor = autor;
        this.totalPaginas = totalPaginas;
        
        System.out.println("Novo livro registrado: " + this.titulo);
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getAutor() {
        return this.autor;
    }

    public int getTotalPaginas() {
        return this.totalPaginas;
    }
}