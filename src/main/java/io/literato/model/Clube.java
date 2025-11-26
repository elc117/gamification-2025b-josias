package io.literato.model;

public class Clube {
    private int id;
    private String nome;
    private String descricao;
    private String foto; // URL da imagem
    private String capa; // URL da imagem de capa (banner)
    private int donoId;  // ID do usuário que criou
    private boolean publico = true; // Por padrão é público

    public Clube() {}

    public Clube(int id, String nome, String descricao, String foto, String capa, int donoId, boolean publico) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.foto = foto;
        this.capa = capa;
        this.donoId = donoId;
        this.publico = publico;
    }
    
    public Clube(int id, String nome, String descricao, String foto, int donoId, boolean publico) {
        this(id, nome, descricao, foto, null, donoId, publico);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getCapa() { return capa; }
    public void setCapa(String capa) { this.capa = capa; }

    public int getDonoId() { return donoId; }
    public void setDonoId(int donoId) { this.donoId = donoId; }

    public boolean isPublico() { return publico; }
    public void setPublico(boolean publico) { this.publico = publico; }
}