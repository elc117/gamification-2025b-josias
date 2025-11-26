package io.literato.model;

public class EstatisticasUsuario {
    private int totalPaginasLidas;
    private int totalLivrosLidos; 
    private int totalLivrosNaEstante;
    private int totalLivrosConcluidosEstante;
    private int totalClubesParticipando;
    private int totalClubesCriados;
    private int streakAtual;
    private int pontosTotais;

    public EstatisticasUsuario(int totalPaginasLidas, int totalLivrosLidos, int totalLivrosNaEstante,
                               int totalLivrosConcluidosEstante, int totalClubesParticipando,
                               int totalClubesCriados, int streakAtual, int pontosTotais) {
        this.totalPaginasLidas = totalPaginasLidas;
        this.totalLivrosLidos = totalLivrosLidos;
        this.totalLivrosNaEstante = totalLivrosNaEstante;
        this.totalLivrosConcluidosEstante = totalLivrosConcluidosEstante;
        this.totalClubesParticipando = totalClubesParticipando;
        this.totalClubesCriados = totalClubesCriados;
        this.streakAtual = streakAtual;
        this.pontosTotais = pontosTotais;
    }

    public int getTotalPaginasLidas() { return totalPaginasLidas; }
    public int getTotalLivrosLidos() { return totalLivrosLidos; }
    public int getTotalLivrosNaEstante() { return totalLivrosNaEstante; }
    public int getTotalLivrosConcluidosEstante() { return totalLivrosConcluidosEstante; }
    public int getTotalClubesParticipando() { return totalClubesParticipando; }
    public int getTotalClubesCriados() { return totalClubesCriados; }
    public int getStreakAtual() { return streakAtual; }
    public int getPontosTotais() { return pontosTotais; }
}
