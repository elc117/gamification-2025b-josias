package io.literato.model.conquista;

import io.literato.model.EstatisticasUsuario;

public class ConquistaClubes extends ConquistaBase {
    public ConquistaClubes(String nome, int bonus, int alvo) {
        super(nome, "Participar de " + alvo + " clubes", bonus, alvo);
    }

    @Override
    public int getProgressoAtual(EstatisticasUsuario stats) {
        return stats.getTotalClubesParticipando();
    }
}
