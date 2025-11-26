package io.literato.model;

public class Leitura {
    private Integer id;
    private Integer usuarioId;
    private String livroTitulo;
    private int paginasLidas;
    private String dataLeitura; 

    public Leitura() {}

    public Leitura(Integer usuarioId, String livroTitulo, int paginasLidas) {
        this.usuarioId = usuarioId;
        this.livroTitulo = livroTitulo;
        this.paginasLidas = paginasLidas;
    }

    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getLivroTitulo() { return livroTitulo; }
    public void setLivroTitulo(String livroTitulo) { this.livroTitulo = livroTitulo; }

    public int getPaginasLidas() { return paginasLidas; }
    public void setPaginasLidas(int paginasLidas) { this.paginasLidas = paginasLidas; }

    public String getDataLeitura() { return dataLeitura; }
    public void setDataLeitura(String dataLeitura) { this.dataLeitura = dataLeitura; }
}