package io.literato.model;

public class ConquistaStatusDTO {
    private String nome;
    private String descricao;
    private int bonus;
    private boolean alcancada;
    private int progressoAtual;
    private int progressoAlvo;

    public ConquistaStatusDTO(String nome, String descricao, int bonus, boolean alcancada, int progressoAtual, int progressoAlvo) {
        this.nome = nome;
        this.descricao = descricao;
        this.bonus = bonus;
        this.alcancada = alcancada;
        this.progressoAtual = progressoAtual;
        this.progressoAlvo = progressoAlvo;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getBonus() { return bonus; }
    public boolean isAlcancada() { return alcancada; }
    public int getProgressoAtual() { return progressoAtual; }
    public int getProgressoAlvo() { return progressoAlvo; }
    
    public int getPorcentagem() {
        if (progressoAlvo == 0) return 100;
        int pct = (int) ((double) progressoAtual / progressoAlvo * 100);
        return Math.min(pct, 100);
    }
}
