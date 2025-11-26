package io.literato.model.conquista;

import io.literato.model.EstatisticasUsuario;

public class ConquistaStreak extends ConquistaBase {
    public ConquistaStreak(String nome, int bonus, int alvo) {
        super(nome, "Manter um streak de " + alvo + " dias", bonus, alvo);
    }

    @Override
    public int getProgressoAtual(EstatisticasUsuario stats) {
        return stats.getStreakAtual();
    }
}
