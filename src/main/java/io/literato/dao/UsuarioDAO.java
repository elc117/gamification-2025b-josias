package io.literato.dao;

import io.literato.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Usuario autenticar(String nome, String senha) {
        String sql = "SELECT * FROM usuarios WHERE nome = ? AND senha = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro na autenticação: " + e.getMessage());
        }
        return null;
    }

    public void adicionarPontos(int usuarioId, int pontosGanhos) {
        String sql = "UPDATE usuarios SET pontos = pontos + ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pontosGanhos);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void removerPontos(int usuarioId, int pontosPerdidos) {
        String sql = "UPDATE usuarios SET pontos = pontos - ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pontosPerdidos);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao remover pontos: " + e.getMessage());
        }
    }

    public void atualizarStreak(int usuarioId, int novoStreak, String dataHoje) {
        String sql = "UPDATE usuarios SET streak = ?, ultima_leitura = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, novoStreak);
            stmt.setString(2, dataHoje);
            stmt.setInt(3, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Usuario> listarRankingTop10() {
        return listarRankingFiltrado(null, "SEMPRE");
    }

    public List<Usuario> listarRankingClube(int clubeId) {
        return listarRankingFiltrado(clubeId, "SEMPRE");
    }

    public List<Usuario> listarRankingFiltrado(Integer clubeId, String periodo) {
        List<Usuario> ranking = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        
        sql.append("SELECT u.id, u.nome, u.foto, u.bio, u.streak, u.ultima_leitura, u.senha, ");
        sql.append("COALESCE(SUM(l.paginas_lidas), 0) as pontos ");
        sql.append("FROM usuarios u ");
        sql.append("LEFT JOIN leituras l ON u.id = l.usuario_id "); 

        if (clubeId != null) {
            sql.append("JOIN membros_clube mc ON u.id = mc.usuario_id ");
        }

        sql.append("WHERE 1=1 ");

        if (clubeId != null) {
            sql.append("AND mc.clube_id = ? ");
            params.add(clubeId);
        }

        java.time.LocalDate dataInicio = java.time.LocalDate.now();
        if ("DIA".equalsIgnoreCase(periodo)) {
            sql.append("AND l.data_leitura = ? ");
            params.add(dataInicio.toString());
        } else if ("SEMANA".equalsIgnoreCase(periodo)) {
            sql.append("AND l.data_leitura >= ? ");
            params.add(dataInicio.minusDays(7).toString());
        } else if ("MES".equalsIgnoreCase(periodo)) {
            sql.append("AND l.data_leitura >= ? ");
            params.add(dataInicio.withDayOfMonth(1).toString());
        }

        sql.append("GROUP BY u.id ");
        sql.append("ORDER BY pontos DESC LIMIT 10");

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Usuario u = mapearUsuario(rs);
                u.setPontos(rs.getInt("pontos"));
                ranking.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar ranking filtrado: " + e.getMessage());
        }
        return ranking;
    }


    public void salvar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, senha, pontos, streak, bio, foto) VALUES (?, ?, 0, 0, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, "Olá, sou novo aqui!"); 
            stmt.setString(4, "https://ui-avatars.com/api/?name=" + usuario.getNome()); 
            stmt.executeUpdate();
        }
    }

    public void atualizarPerfil(int id, String bio, String foto) throws SQLException {
        String sql = "UPDATE usuarios SET bio = ?, foto = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bio);
            stmt.setString(2, foto);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public int contarPaginasLidas(int usuarioId) {
        String sql = "SELECT SUM(paginas_lidas) FROM leituras WHERE usuario_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int contarLivrosLidos(int usuarioId) {
        String sql = "SELECT COUNT(DISTINCT livro_titulo) FROM leituras WHERE usuario_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("senha"),
            rs.getInt("pontos"),
            rs.getInt("streak"),
            rs.getString("ultima_leitura"),
            rs.getString("bio"),
            rs.getString("foto")
        );
    }
}