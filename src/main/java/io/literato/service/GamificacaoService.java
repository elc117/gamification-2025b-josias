package io.literato.service;

import io.literato.dao.ClubeDAO;
import io.literato.dao.EstanteDAO;
import io.literato.dao.LeituraDAO;
import io.literato.dao.UsuarioDAO;
import io.literato.model.Clube;
import io.literato.model.ItemEstante;
import io.literato.model.Leitura;
import io.literato.model.Usuario;
import io.literato.model.StatusLeitura;
import io.literato.model.conquista.*;
import io.literato.model.EstatisticasUsuario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GamificacaoService {

    private final UsuarioDAO usuarioDAO;
    private final LeituraDAO leituraDAO;
    private final ClubeDAO clubeDAO;
    private final EstanteDAO estanteDAO;
    private final List<Conquista> conquistasDisponiveis;

    public GamificacaoService(UsuarioDAO usuarioDAO, LeituraDAO leituraDAO, ClubeDAO clubeDAO, EstanteDAO estanteDAO) {
        this.usuarioDAO = usuarioDAO;
        this.leituraDAO = leituraDAO;
        this.clubeDAO = clubeDAO;
        this.estanteDAO = estanteDAO;
        
        this.conquistasDisponiveis = new ArrayList<>();
        inicializarConquistas();
    }

    private void inicializarConquistas() {
        // Páginas Lidas
        conquistasDisponiveis.add(new ConquistaPaginas("Leitor Iniciante", 10, 50));
        conquistasDisponiveis.add(new ConquistaPaginas("Leitor Ávido", 50, 500));
        conquistasDisponiveis.add(new ConquistaPaginas("Devorador de Livros", 100, 1000));
        conquistasDisponiveis.add(new ConquistaPaginas("Biblioteca Ambulante", 500, 5000));

        // Streak
        conquistasDisponiveis.add(new ConquistaStreak("Aquecimento", 20, 3));
        conquistasDisponiveis.add(new ConquistaStreak("Hábito Formado", 50, 7));
        conquistasDisponiveis.add(new ConquistaStreak("Viciado em Leitura", 150, 30));
        conquistasDisponiveis.add(new ConquistaStreak("Mestre da Constância", 500, 100));

        // Pontos (XP)
        conquistasDisponiveis.add(new ConquistaPontos("Primeiros Passos", 10, 100));
        conquistasDisponiveis.add(new ConquistaPontos("Subindo de Nível", 100, 1000));
        conquistasDisponiveis.add(new ConquistaPontos("Lenda Literária", 1000, 10000));

        // Estante
        conquistasDisponiveis.add(new ConquistaEstante("Colecionador", 30, 5));
        conquistasDisponiveis.add(new ConquistaEstante("Estante Cheia", 100, 20));
        
        // Leitura Concluída
        conquistasDisponiveis.add(new ConquistaLeituraConcluida("Primeira de Muitas", 50, 1));
        conquistasDisponiveis.add(new ConquistaLeituraConcluida("Maratona Literária", 200, 5));

        // Clubes
        conquistasDisponiveis.add(new ConquistaClubes("Socialite", 20, 1));
        conquistasDisponiveis.add(new ConquistaClubes("Membro Ativo", 50, 3));
    }

    // estante

    public void iniciarLeitura(ItemEstante item) throws SQLException {
        this.estanteDAO.adicionar(item);
    }

    public void registrarProgresso(int itemEstanteId, int paginasLidasHoje) throws SQLException {
        ItemEstante item = this.estanteDAO.buscarPorId(itemEstanteId);
        if (item == null) throw new IllegalArgumentException("Item não encontrado");

        try {
            item.registrarProgresso(paginasLidasHoje);
        } catch (IllegalArgumentException e) {
            throw e; 
        }
        
        // 1. Atualiza a Estante
        this.estanteDAO.atualizarProgresso(itemEstanteId, item.getPaginasLidas(), item.getStatus());

        // 2. Registra no Histórico (Log) para ganhar pontos
        Leitura leituraLog = new Leitura();
        leituraLog.setUsuarioId(item.getUsuarioId());
        leituraLog.setLivroTitulo(item.getTitulo());
        leituraLog.setPaginasLidas(paginasLidasHoje);
        leituraLog.setDataLeitura(LocalDate.now().toString());
        
        // Reutiliza a lógica existente de pontos e streak
        registrarLeitura(leituraLog);
    }

    public List<ItemEstante> listarEstante(int usuarioId) {
        return this.estanteDAO.listarPorUsuario(usuarioId);
    }


    public void registrarLeitura(Leitura leitura) throws SQLException {
        this.leituraDAO.salvar(leitura);
        
        Usuario usuario = this.usuarioDAO.buscarPorId(leitura.getUsuarioId());
        if (usuario != null) {
            usuario.ganharPontos(leitura.getPaginasLidas());
            usuario.registrarLeitura(LocalDate.now());

            this.usuarioDAO.adicionarPontos(usuario.getId(), leitura.getPaginasLidas());
            this.usuarioDAO.atualizarStreak(usuario.getId(), usuario.getStreak(), usuario.getUltimaLeitura());
        }

        verificarConquistas(leitura.getUsuarioId());
    }

    private EstatisticasUsuario coletarEstatisticas(int usuarioId) {
        Usuario usuario = this.usuarioDAO.buscarPorId(usuarioId);
        if (usuario == null) return null;

        int paginas = this.usuarioDAO.contarPaginasLidas(usuarioId);
        int livrosLidos = this.usuarioDAO.contarLivrosLidos(usuarioId);
        int estante = this.estanteDAO.contarLivrosNaEstante(usuarioId);
        int concluidos = this.estanteDAO.contarLivrosConcluidos(usuarioId);
        int clubes = this.clubeDAO.contarClubesParticipando(usuarioId);
        int criados = this.clubeDAO.contarClubesCriados(usuarioId);

        return new EstatisticasUsuario(paginas, livrosLidos, estante, concluidos, clubes, criados, usuario.getStreak(), usuario.getPontos());
    }

    private void verificarConquistas(int usuarioId) {
        EstatisticasUsuario stats = coletarEstatisticas(usuarioId);
        if (stats == null) return;

        System.out.println("--- Verificando Conquistas para ID " + usuarioId + " ---");
        for (Conquista conquista : conquistasDisponiveis) {
            if (conquista.isAlcancada(stats)) {
                System.out.println("🏆 CONQUISTA DESBLOQUEADA: " + conquista.getNome());
            }
        }
        System.out.println("---------------------------------------------------");
    }

    public Usuario buscarStatusUsuario(int usuarioId) {
        return this.usuarioDAO.buscarPorId(usuarioId);
    }

    public Usuario login(String nome, String senha) {
        return this.usuarioDAO.autenticar(nome, senha);
    }

    public void criarUsuario(Usuario usuario) throws SQLException {
        this.usuarioDAO.salvar(usuario);
    }

    public void atualizarPerfil(int id, String bio, String foto) throws SQLException {
        this.usuarioDAO.atualizarPerfil(id, bio, foto);
    }
    
    public void criarClube(Clube clube) throws SQLException {
        this.clubeDAO.criar(clube);
    }

    public List<Clube> listarClubes() {
        return this.clubeDAO.listarTodos();
    }

    public void entrarNoClube(int usuarioId, int clubeId) throws SQLException {
        Clube clube = this.clubeDAO.buscarPorId(clubeId);
        if (clube == null) throw new IllegalArgumentException("Clube não encontrado");

        if (clube.isPublico()) {
            this.clubeDAO.entrar(usuarioId, clubeId);
        } else {
            if (this.clubeDAO.isMembro(usuarioId, clubeId)) {
                throw new IllegalArgumentException("Você já é membro deste clube.");
            }
            if (this.clubeDAO.existeSolicitacaoPendente(usuarioId, clubeId)) {
                throw new IllegalArgumentException("Solicitação já enviada. Aguarde aprovação.");
            }
            this.clubeDAO.solicitarEntrada(usuarioId, clubeId);
            throw new IllegalStateException("Solicitação enviada para o dono do clube."); // Usando exceção para sinalizar ao controller que não entrou direto
        }
    }

    public List<java.util.Map<String, Object>> listarSolicitacoesClube(int clubeId, int usuarioLogadoId) {
        Clube clube = this.clubeDAO.buscarPorId(clubeId);
        if (clube == null || clube.getDonoId() != usuarioLogadoId) {
            throw new SecurityException("Apenas o dono pode ver solicitações.");
        }
        return this.clubeDAO.listarSolicitacoes(clubeId);
    }

    public void responderSolicitacaoClube(int solicitacaoId, boolean aceitar, int usuarioLogadoId) throws SQLException {
        this.clubeDAO.responderSolicitacao(solicitacaoId, aceitar);
    }
    


    public void atualizarStatusEstante(int id, String novoStatusStr) throws SQLException {
        // Se marcar como CONCLUIDO, calcula o restante das páginas e atribui os pontos
        if ("CONCLUIDO".equals(novoStatusStr)) {
            ItemEstante item = this.estanteDAO.buscarPorId(id);
            if (item != null) {
                int restantes = item.getPaginasTotal() - item.getPaginasLidas();
                if (restantes > 0) {
                    // Reutiliza registrarProgresso para dar os pontos, atualizar páginas e status
                    registrarProgresso(id, restantes);
                    return;
                }
            }
        }
        // Para outros casos (ABANDONADO) ou se já leu tudo, apenas muda o status
        try {
            StatusLeitura status = StatusLeitura.valueOf(novoStatusStr);
            this.estanteDAO.atualizarStatus(id, status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + novoStatusStr);
        }
    }

    public void removerDaEstante(int id) throws SQLException {
        ItemEstante item = this.estanteDAO.buscarPorId(id);
        if (item != null && item.getPaginasLidas() > 0) {
            // Remove XP equivalente ao que foi lido
            Leitura leituraCorrecao = new Leitura();
            leituraCorrecao.setUsuarioId(item.getUsuarioId());
            leituraCorrecao.setLivroTitulo(item.getTitulo() + " (Removido)");
            leituraCorrecao.setPaginasLidas(-item.getPaginasLidas()); // Negativo
            leituraCorrecao.setDataLeitura(LocalDate.now().toString());
            
            registrarLeitura(leituraCorrecao);
        }
        this.estanteDAO.excluir(id);
    }

    public void editarProgressoAbsoluto(int itemEstanteId, int novasPaginasLidas) throws SQLException {
        ItemEstante item = this.estanteDAO.buscarPorId(itemEstanteId);
        if (item == null) throw new IllegalArgumentException("Item não encontrado");

        if (novasPaginasLidas < 0 || novasPaginasLidas > item.getPaginasTotal()) {
            throw new IllegalArgumentException("Número de páginas inválido");
        }

        int diferenca = novasPaginasLidas - item.getPaginasLidas();
        
        if (diferenca != 0) {
            // Registra a diferença 
            Leitura leituraCorrecao = new Leitura();
            leituraCorrecao.setUsuarioId(item.getUsuarioId());
            leituraCorrecao.setLivroTitulo(item.getTitulo() + " (Correção)");
            leituraCorrecao.setPaginasLidas(diferenca);
            leituraCorrecao.setDataLeitura(LocalDate.now().toString());
            
            registrarLeitura(leituraCorrecao);
        }

        StatusLeitura novoStatus = (novasPaginasLidas == item.getPaginasTotal()) ? StatusLeitura.CONCLUIDO : StatusLeitura.LENDO;
        this.estanteDAO.atualizarProgresso(itemEstanteId, novasPaginasLidas, novoStatus);
    }

    public List<Leitura> obterHistoricoRecente(int usuarioId) throws SQLException {
        List<Leitura> todoHistorico = this.leituraDAO.listarPorUsuario(usuarioId);
        int tamanho = todoHistorico.size();
        int inicio = Math.max(0, tamanho - 5);
        List<Leitura> recentes = new ArrayList<>(todoHistorico.subList(inicio, tamanho));
        java.util.Collections.reverse(recentes);
        return recentes;
    }

    public List<io.literato.model.ConquistaStatusDTO> calcularConquistas(Usuario usuario) {
        EstatisticasUsuario stats = coletarEstatisticas(usuario.getId());
        List<io.literato.model.ConquistaStatusDTO> statusConquistas = new ArrayList<>();
        
        for (Conquista c : conquistasDisponiveis) {
            boolean alcancada = c.isAlcancada(stats);
            int progresso = c.getProgressoAtual(stats);
            int alvo = c.getProgressoAlvo();
            
            statusConquistas.add(new io.literato.model.ConquistaStatusDTO(
                c.getNome(), 
                c.getDescricao(), 
                c.getBonus(), 
                alcancada,
                progresso,
                alvo
            ));
        }
        return statusConquistas;
    }

    public io.literato.model.PerfilDTO obterPerfilCompleto(int usuarioId) throws SQLException {
        Usuario usuario = this.usuarioDAO.buscarPorId(usuarioId);
        if (usuario == null) return null;

        List<io.literato.model.ConquistaStatusDTO> conquistas = calcularConquistas(usuario);
        List<Leitura> historico = obterHistoricoRecente(usuarioId);
        List<Clube> clubes = this.clubeDAO.listarPorMembro(usuarioId);
        List<ItemEstante> estante = this.estanteDAO.listarPorUsuario(usuarioId);
        List<Integer> solicitacoes = this.clubeDAO.listarIdsClubesComSolicitacaoPendente(usuarioId);

        return new io.literato.model.PerfilDTO(usuario, conquistas, historico, clubes, estante, solicitacoes);
    }

    public List<Usuario> obterRanking(Integer clubeId, String periodo) {
        return this.usuarioDAO.listarRankingFiltrado(clubeId, periodo);
    }

    public List<Usuario> obterRanking(Integer clubeId) {
        return obterRanking(clubeId, "SEMPRE");
    }
    
    public List<Usuario> obterRanking() {
        return obterRanking(null, "SEMPRE");
    }

    // --- GESTÃO DE CLUBES ---

    public void excluirClube(int clubeId, int usuarioId) throws SQLException {
        Clube clube = this.clubeDAO.buscarPorId(clubeId);
        if (clube == null) throw new IllegalArgumentException("Clube não encontrado");
        if (clube.getDonoId() != usuarioId) throw new SecurityException("Apenas o dono pode excluir o clube");
        
        this.clubeDAO.excluir(clubeId);
    }

    public void sairDoClube(int clubeId, int usuarioId) throws SQLException {
        Clube clube = this.clubeDAO.buscarPorId(clubeId);
        if (clube == null) throw new IllegalArgumentException("Clube não encontrado");
        
        if (clube.getDonoId() == usuarioId) {
            throw new IllegalStateException("O dono não pode sair do clube. Transfira a posse ou exclua o clube.");
        }
        
        this.clubeDAO.sair(clubeId, usuarioId);
    }

    public void alterarCargoMembro(int clubeId, int usuarioAlvoId, String novoCargo, int usuarioLogadoId) throws SQLException {
        Clube clube = this.clubeDAO.buscarPorId(clubeId);
        if (clube == null) throw new IllegalArgumentException("Clube não encontrado");
        if (clube.getDonoId() != usuarioLogadoId) throw new SecurityException("Apenas o dono pode alterar cargos");
        if (usuarioAlvoId == usuarioLogadoId) throw new IllegalArgumentException("Você não pode alterar seu próprio cargo aqui");

        if ("DONO".equals(novoCargo)) {
            this.clubeDAO.transferirDono(clubeId, usuarioAlvoId, usuarioLogadoId);
        } else {
            this.clubeDAO.atualizarCargo(clubeId, usuarioAlvoId, novoCargo);
        }
    }

    public List<java.util.Map<String, Object>> listarMembrosClubeComCargo(int clubeId) {
        return this.clubeDAO.listarMembrosComCargo(clubeId);
    }

    public void atualizarClube(int clubeId, Clube dadosAtualizados, int usuarioLogadoId) throws SQLException {
        Clube clube = this.clubeDAO.buscarPorId(clubeId);
        if (clube == null) throw new IllegalArgumentException("Clube não encontrado");
        
        String cargo = this.clubeDAO.buscarCargo(usuarioLogadoId, clubeId);
        if (!"DONO".equals(cargo) && !"MODERADOR".equals(cargo)) {
            throw new SecurityException("Apenas Dono ou Moderador podem editar o clube.");
        }

        clube.setNome(dadosAtualizados.getNome());
        clube.setDescricao(dadosAtualizados.getDescricao());
        clube.setFoto(dadosAtualizados.getFoto());
        clube.setCapa(dadosAtualizados.getCapa());
        clube.setPublico(dadosAtualizados.isPublico());

        this.clubeDAO.atualizar(clube);
    }

    public void expulsarMembro(int clubeId, int usuarioExpulsoId, int usuarioLogadoId) throws SQLException {
        Clube clube = this.clubeDAO.buscarPorId(clubeId);
        if (clube == null) throw new IllegalArgumentException("Clube não encontrado");

        String cargoLogado = this.clubeDAO.buscarCargo(usuarioLogadoId, clubeId);
        String cargoExpulso = this.clubeDAO.buscarCargo(usuarioExpulsoId, clubeId);

        if (cargoLogado == null) throw new SecurityException("Você não é membro deste clube.");
        if (cargoExpulso == null) throw new IllegalArgumentException("Usuário não é membro deste clube.");

        boolean podeExpulsar = false;

        if ("DONO".equals(cargoLogado)) {
            if (usuarioExpulsoId != usuarioLogadoId) podeExpulsar = true;
        } else if ("MODERADOR".equals(cargoLogado)) {
            if ("MEMBRO".equals(cargoExpulso)) podeExpulsar = true;
        }

        if (!podeExpulsar) {
            throw new SecurityException("Você não tem permissão para expulsar este membro.");
        }

        this.clubeDAO.sair(clubeId, usuarioExpulsoId);
    }
}