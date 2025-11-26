package io.literato.dao;

import io.literato.model.ItemEstante;
import io.literato.model.StatusLeitura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstanteDAO {

    public void adicionar(ItemEstante item) throws SQLException {
        String sql = "INSERT INTO estante (usuario_id, livro_id, titulo, autor, paginas_total, paginas_lidas, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getUsuarioId());
            stmt.setInt(2, item.getLivroId());
            stmt.setString(3, item.getTitulo());
            stmt.setString(4, item.getAutor());
            stmt.setInt(5, item.getPaginasTotal());
            stmt.setInt(6, 0);
            stmt.setString(7, StatusLeitura.LENDO.name());
            stmt.executeUpdate();
        }
    }

    public void atualizarProgresso(int id, int novasPaginas, StatusLeitura status) throws SQLException {
        String sql = "UPDATE estante SET paginas_lidas = ?, status = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, novasPaginas);
            stmt.setString(2, status.name());
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarStatus(int id, StatusLeitura status) throws SQLException {
        String sql = "UPDATE estante SET status = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public List<ItemEstante> listarPorUsuario(int usuarioId) {
        List<ItemEstante> itens = new ArrayList<>();
        String sql = "SELECT * FROM estante WHERE usuario_id = ? ORDER BY status DESC, id DESC";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                itens.add(new ItemEstante(
                    rs.getInt("id"),
                    rs.getInt("usuario_id"),
                    rs.getInt("livro_id"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getInt("paginas_total"),
                    rs.getInt("paginas_lidas"),
                    StatusLeitura.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar estante: " + e.getMessage());
        }
        return itens;
    }

    public ItemEstante buscarPorId(int id) {
        String sql = "SELECT * FROM estante WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ItemEstante(
                    rs.getInt("id"),
                    rs.getInt("usuario_id"),
                    rs.getInt("livro_id"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getInt("paginas_total"),
                    rs.getInt("paginas_lidas"),
                    StatusLeitura.valueOf(rs.getString("status"))
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar item estante: " + e.getMessage());
        }
        return null;
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM estante WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public int contarLivrosNaEstante(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM estante WHERE usuario_id = ?";
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

    public int contarLivrosConcluidos(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM estante WHERE usuario_id = ? AND status = 'CONCLUIDO'";
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
}
