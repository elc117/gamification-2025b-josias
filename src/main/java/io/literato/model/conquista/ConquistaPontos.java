package io.literato.model.conquista;

import io.literato.model.EstatisticasUsuario;

public class ConquistaPontos extends ConquistaBase {
    public ConquistaPontos(String nome, int bonus, int alvo) {
        super(nome, "Alcançar " + alvo + " pontos", bonus, alvo);
    }

    @Override
    public int getProgressoAtual(EstatisticasUsuario stats) {
        return stats.getPontosTotais();
    }
}
