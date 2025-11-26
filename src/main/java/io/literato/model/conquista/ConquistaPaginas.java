package io.literato.model.conquista;

import io.literato.model.EstatisticasUsuario;

public class ConquistaPaginas extends ConquistaBase {
    public ConquistaPaginas(String nome, int bonus, int alvo) {
        super(nome, "Ler " + alvo + " páginas no total", bonus, alvo);
    }

    @Override
    public int getProgressoAtual(EstatisticasUsuario stats) {
        return stats.getTotalPaginasLidas();
    }
}
