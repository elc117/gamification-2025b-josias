package io.literato.model;

public class Livro {
    private int id;
    private String titulo;
    private String autor;
    private double notaMedia;
    private String isbn;
    private int paginas;

    public Livro() {}

    public Livro(int id, String titulo, String autor, double notaMedia, String isbn, int paginas) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.notaMedia = notaMedia;
        this.isbn = isbn;
        this.paginas = paginas;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public double getNotaMedia() { return notaMedia; }
    public void setNotaMedia(double notaMedia) { this.notaMedia = notaMedia; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getPaginas() { return paginas; }
    public void setPaginas(int paginas) { this.paginas = paginas; }
}
