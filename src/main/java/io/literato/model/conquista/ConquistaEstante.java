package io.literato.model.conquista;

import io.literato.model.EstatisticasUsuario;

public class ConquistaEstante extends ConquistaBase {
    public ConquistaEstante(String nome, int bonus, int alvo) {
        super(nome, "Ter " + alvo + " livros na estante", bonus, alvo);
    }

    @Override
    public int getProgressoAtual(EstatisticasUsuario stats) {
        return stats.getTotalLivrosNaEstante();
    }
}
