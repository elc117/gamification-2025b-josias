package io.literato.model;

public class ItemEstante {
    private int id;
    private int usuarioId;
    private int livroId;
    private String titulo;
    private String autor;
    private int paginasTotal;
    private int paginasLidas;
    private StatusLeitura status;

    public ItemEstante() {}

    public ItemEstante(int id, int usuarioId, int livroId, String titulo, String autor, int paginasTotal, int paginasLidas, StatusLeitura status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.titulo = titulo;
        this.autor = autor;
        this.paginasTotal = paginasTotal;
        this.paginasLidas = paginasLidas;
        this.status = status;
    }

    // Métodos de Negócio (Rich Domain Model)
    public void registrarProgresso(int paginasLidasHoje) {
        if (paginasLidasHoje < 0) throw new IllegalArgumentException("Páginas lidas não pode ser negativo");
        
        this.paginasLidas += paginasLidasHoje;
        
        if (this.paginasLidas >= this.paginasTotal) {
            this.paginasLidas = this.paginasTotal;
            concluir();
        } else {
            this.status = StatusLeitura.LENDO;
        }
    }

    public void concluir() {
        this.status = StatusLeitura.CONCLUIDO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getLivroId() { return livroId; }
    public void setLivroId(int livroId) { this.livroId = livroId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public int getPaginasTotal() { return paginasTotal; }
    public void setPaginasTotal(int paginasTotal) { this.paginasTotal = paginasTotal; }

    public int getPaginasLidas() { return paginasLidas; }
    public void setPaginasLidas(int paginasLidas) { this.paginasLidas = paginasLidas; }

    public StatusLeitura getStatus() { return status; }
    public void setStatus(StatusLeitura status) { this.status = status; }
}
