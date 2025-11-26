package io.literato.model.conquista;

import io.literato.model.EstatisticasUsuario;

public class ConquistaLeituraConcluida extends ConquistaBase {
    public ConquistaLeituraConcluida(String nome, int bonus, int alvo) {
        super(nome, "Concluir a leitura de " + alvo + " livros", bonus, alvo);
    }

    @Override
    public int getProgressoAtual(EstatisticasUsuario stats) {
        return stats.getTotalLivrosConcluidosEstante();
    }
}
