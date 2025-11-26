package io.literato.dao;

import io.literato.model.Clube;
import io.literato.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClubeDAO {

    public void criar(Clube clube) throws SQLException {
        String sql = "INSERT INTO clubes (nome, descricao, foto, capa, dono_id, publico) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, clube.getNome());
            stmt.setString(2, clube.getDescricao());
            stmt.setString(3, clube.getFoto());
            stmt.setString(4, clube.getCapa());
            stmt.setInt(5, clube.getDonoId());
            stmt.setBoolean(6, clube.isPublico());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int novoClubeId = generatedKeys.getInt(1);
                    entrar(clube.getDonoId(), novoClubeId, "DONO");
                }
            }
        }
    }

    public void atualizar(Clube clube) throws SQLException {
        String sql = "UPDATE clubes SET nome = ?, descricao = ?, foto = ?, capa = ?, publico = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clube.getNome());
            stmt.setString(2, clube.getDescricao());
            stmt.setString(3, clube.getFoto());
            stmt.setString(4, clube.getCapa());
            stmt.setBoolean(5, clube.isPublico());
            stmt.setInt(6, clube.getId());
            stmt.executeUpdate();
        }
    }

    public List<Clube> listarTodos() {
        List<Clube> clubes = new ArrayList<>();
        String sql = "SELECT * FROM clubes";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Clube c = new Clube(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getString("foto"),
                    rs.getString("capa"),
                    rs.getInt("dono_id"),
                    rs.getBoolean("publico")
                );
                clubes.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar clubes: " + e.getMessage());
        }
        return clubes;
    }

    public void entrar(int usuarioId, int clubeId) throws SQLException {
        entrar(usuarioId, clubeId, "MEMBRO");
    }

    public void entrar(int usuarioId, int clubeId, String cargo) throws SQLException {
        if (isMembro(usuarioId, clubeId)) return;

        String sql = "INSERT INTO membros_clube (usuario_id, clube_id, cargo) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, clubeId);
            stmt.setString(3, cargo);
            stmt.executeUpdate();
        }
    }

    public boolean isMembro(int usuarioId, int clubeId) {
        String sql = "SELECT 1 FROM membros_clube WHERE usuario_id = ? AND clube_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, clubeId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public void excluir(int clubeId) throws SQLException {
        String sqlMembros = "DELETE FROM membros_clube WHERE clube_id = ?";
        String sqlSolicitacoes = "DELETE FROM solicitacoes_clube WHERE clube_id = ?";
        String sqlClube = "DELETE FROM clubes WHERE id = ?";

        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false); 
            try {
                try (PreparedStatement stmt = conn.prepareStatement(sqlMembros)) {
                    stmt.setInt(1, clubeId);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement(sqlSolicitacoes)) {
                    stmt.setInt(1, clubeId);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement(sqlClube)) {
                    stmt.setInt(1, clubeId);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void sair(int clubeId, int usuarioId) throws SQLException {
        String sql = "DELETE FROM membros_clube WHERE clube_id = ? AND usuario_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubeId);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        }
    }

    public void atualizarCargo(int clubeId, int usuarioId, String novoCargo) throws SQLException {
        String sql = "UPDATE membros_clube SET cargo = ? WHERE clube_id = ? AND usuario_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoCargo);
            stmt.setInt(2, clubeId);
            stmt.setInt(3, usuarioId);
            stmt.executeUpdate();
        }
    }

    public void transferirDono(int clubeId, int novoDonoId, int antigoDonoId) throws SQLException {
        String sqlUpdateClube = "UPDATE clubes SET dono_id = ? WHERE id = ?";
        String sqlUpdateNovoDono = "UPDATE membros_clube SET cargo = 'DONO' WHERE clube_id = ? AND usuario_id = ?";
        String sqlUpdateAntigoDono = "UPDATE membros_clube SET cargo = 'MODERADOR' WHERE clube_id = ? AND usuario_id = ?";

        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);
            try {
                // 1. Atualiza tabela clubes
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateClube)) {
                    stmt.setInt(1, novoDonoId);
                    stmt.setInt(2, clubeId);
                    stmt.executeUpdate();
                }
                // 2. Promove novo dono
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateNovoDono)) {
                    stmt.setInt(1, clubeId);
                    stmt.setInt(2, novoDonoId);
                    stmt.executeUpdate();
                }
                // 3. Rebaixa antigo dono
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateAntigoDono)) {
                    stmt.setInt(1, clubeId);
                    stmt.setInt(2, antigoDonoId);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<java.util.Map<String, Object>> listarMembrosComCargo(int clubeId) {
        List<java.util.Map<String, Object>> membros = new ArrayList<>();
        String sql = """
            SELECT u.id, u.nome, u.foto, u.pontos, u.streak, mc.cargo 
            FROM usuarios u
            JOIN membros_clube mc ON u.id = mc.usuario_id
            WHERE mc.clube_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clubeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("nome", rs.getString("nome"));
                m.put("foto", rs.getString("foto"));
                m.put("pontos", rs.getInt("pontos"));
                m.put("streak", rs.getInt("streak"));
                m.put("cargo", rs.getString("cargo"));
                membros.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar membros: " + e.getMessage());
        }
        return membros;
    }


    public Clube buscarPorId(int id) {
        String sql = "SELECT * FROM clubes WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Clube(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getString("foto"),
                    rs.getString("capa"),
                    rs.getInt("dono_id"),
                    rs.getBoolean("publico")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String buscarCargo(int usuarioId, int clubeId) {
        String sql = "SELECT cargo FROM membros_clube WHERE usuario_id = ? AND clube_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, clubeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("cargo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void solicitarEntrada(int usuarioId, int clubeId) throws SQLException {
        String sql = "INSERT INTO solicitacoes_clube (usuario_id, clube_id, status, data_solicitacao) VALUES (?, ?, 'PENDENTE', date('now'))";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, clubeId);
            stmt.executeUpdate();
        }
    }

    public boolean existeSolicitacaoPendente(int usuarioId, int clubeId) {
        String sql = "SELECT 1 FROM solicitacoes_clube WHERE usuario_id = ? AND clube_id = ? AND status = 'PENDENTE'";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, clubeId);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public List<java.util.Map<String, Object>> listarSolicitacoes(int clubeId) {
        List<java.util.Map<String, Object>> solicitacoes = new ArrayList<>();
        String sql = """
            SELECT s.id, u.nome, u.foto, s.data_solicitacao 
            FROM solicitacoes_clube s
            JOIN usuarios u ON s.usuario_id = u.id
            WHERE s.clube_id = ? AND s.status = 'PENDENTE'
        """;
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("nomeUsuario", rs.getString("nome"));
                map.put("fotoUsuario", rs.getString("foto"));
                map.put("data", rs.getString("data_solicitacao"));
                solicitacoes.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return solicitacoes;
    }

    public void responderSolicitacao(int solicitacaoId, boolean aceitar) throws SQLException {
        String sqlSelect = "SELECT usuario_id, clube_id, status FROM solicitacoes_clube WHERE id = ?";
        String sqlUpdate = "UPDATE solicitacoes_clube SET status = ? WHERE id = ?";
        String sqlInsertMembro = "INSERT INTO membros_clube (usuario_id, clube_id, cargo) VALUES (?, ?, 'MEMBRO')";

        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false);
            try {
                int usuarioId = -1;
                int clubeId = -1;
                String currentStatus = "";

                try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
                    stmt.setInt(1, solicitacaoId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            usuarioId = rs.getInt("usuario_id");
                            clubeId = rs.getInt("clube_id");
                            currentStatus = rs.getString("status");
                            System.out.println("DAO: Solicitação encontrada. Usuario=" + usuarioId + ", Clube=" + clubeId + ", Status=" + currentStatus);
                        } else {
                            throw new SQLException("Solicitação não encontrada ID: " + solicitacaoId);
                        }
                    }
                }

                if (!"PENDENTE".equals(currentStatus)) {
                    System.out.println("DAO: Solicitação já processada (Status: " + currentStatus + "). Retornando sucesso.");
                    conn.commit();
                    return;
                }

                if (aceitar) {
                    boolean jaMembro = false;
                    try (PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM membros_clube WHERE usuario_id = ? AND clube_id = ?")) {
                        stmt.setInt(1, usuarioId);
                        stmt.setInt(2, clubeId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                jaMembro = true;
                                System.out.println("DAO: Usuário já é membro.");
                            }
                        }
                    }

                    if (!jaMembro) {
                        System.out.println("DAO: Inserindo novo membro...");
                        try (PreparedStatement stmt = conn.prepareStatement(sqlInsertMembro)) {
                            stmt.setInt(1, usuarioId);
                            stmt.setInt(2, clubeId);
                            stmt.executeUpdate();
                            System.out.println("DAO: Membro inserido com sucesso.");
                        }
                    }
                }

                System.out.println("DAO: Atualizando status da solicitação...");
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                    stmt.setString(1, aceitar ? "ACEITO" : "REJEITADO");
                    stmt.setInt(2, solicitacaoId);
                    stmt.executeUpdate();
                }

                conn.commit();
                System.out.println("DAO: Transação commitada.");
            } catch (SQLException e) {
                e.printStackTrace();
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Clube> listarPorMembro(int usuarioId) {
        List<Clube> clubes = new ArrayList<>();
        String sql = """
            SELECT c.* FROM clubes c
            JOIN membros_clube mc ON c.id = mc.clube_id
            WHERE mc.usuario_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Clube c = new Clube(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getString("foto"),
                    rs.getInt("dono_id"),
                    rs.getBoolean("publico")
                );
                clubes.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar clubes do membro: " + e.getMessage());
        }
        return clubes;
    }

    public int contarClubesParticipando(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM membros_clube WHERE usuario_id = ?";
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

    public int contarClubesCriados(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM clubes WHERE dono_id = ?";
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

    public List<Integer> listarIdsClubesComSolicitacaoPendente(int usuarioId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT clube_id FROM solicitacoes_clube WHERE usuario_id = ? AND status = 'PENDENTE'";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("clube_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}