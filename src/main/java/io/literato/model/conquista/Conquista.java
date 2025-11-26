package io.literato.model.conquista;

import io.literato.model.EstatisticasUsuario;

public interface Conquista {
    String getNome();
    String getDescricao();
    int getBonus();
    
    boolean isAlcancada(EstatisticasUsuario stats);
    int getProgressoAtual(EstatisticasUsuario stats);
    int getProgressoAlvo();
}
