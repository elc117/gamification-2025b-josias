package io.literato.model;

import java.util.List;

public class PerfilDTO {
    private Usuario usuario;
    private List<ConquistaStatusDTO> conquistas;
    private List<Leitura> historicoRecente;
    private List<Clube> clubes;
    private List<ItemEstante> estante;
    private List<Integer> solicitacoesPendentes;

    public PerfilDTO(Usuario usuario, List<ConquistaStatusDTO> conquistas, List<Leitura> historicoRecente, List<Clube> clubes, List<ItemEstante> estante, List<Integer> solicitacoesPendentes) {
        this.usuario = usuario;
        this.conquistas = conquistas;
        this.historicoRecente = historicoRecente;
        this.clubes = clubes;
        this.estante = estante;
        this.solicitacoesPendentes = solicitacoesPendentes;
    }

    public Usuario getUsuario() { return usuario; }
    public List<ConquistaStatusDTO> getConquistas() { return conquistas; }
    public List<Leitura> getHistoricoRecente() { return historicoRecente; }
    public List<Clube> getClubes() { return clubes; }
    public List<ItemEstante> getEstante() { return estante; }
    public List<Integer> getSolicitacoesPendentes() { return solicitacoesPendentes; }
}
