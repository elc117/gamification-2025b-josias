package io.literato.model.conquista;

import io.literato.model.EstatisticasUsuario;

public abstract class ConquistaBase implements Conquista {
    protected String nome;
    protected String descricao;
    protected int bonus;
    protected int alvo;

    public ConquistaBase(String nome, String descricao, int bonus, int alvo) {
        this.nome = nome;
        this.descricao = descricao;
        this.bonus = bonus;
        this.alvo = alvo;
    }

    @Override
    public String getNome() { return nome; }

    @Override
    public String getDescricao() { return descricao; }

    @Override
    public int getBonus() { return bonus; }

    @Override
    public int getProgressoAlvo() { return alvo; }

    @Override
    public boolean isAlcancada(EstatisticasUsuario stats) {
        return getProgressoAtual(stats) >= alvo;
    }

    // Cada subclasse implementa como obter o progresso atual das estatísticas
    @Override
    public abstract int getProgressoAtual(EstatisticasUsuario stats);
}
