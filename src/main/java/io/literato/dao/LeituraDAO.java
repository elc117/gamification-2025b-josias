// filepath: /workspaces/gamification-2025b-josias/src/main/java/io/literato/dao/LeituraDAO.java
package io.literato.dao;

import io.literato.model.Leitura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeituraDAO {

    public void salvar(Leitura leitura) throws SQLException {
        String sql = "INSERT INTO leituras (usuario_id, livro_titulo, paginas_lidas, data_leitura) VALUES (?, ?, ?, date('now'))";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, leitura.getUsuarioId());
            stmt.setString(2, leitura.getLivroTitulo());
            stmt.setInt(3, leitura.getPaginasLidas());
            
            stmt.executeUpdate();
        }
    }

    public List<Leitura> listarPorUsuario(int usuarioId) throws SQLException {
        List<Leitura> lista = new ArrayList<>();
        String sql = "SELECT * FROM leituras WHERE usuario_id = ? ORDER BY id DESC";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Leitura l = new Leitura();
                l.setId(rs.getInt("id"));
                l.setUsuarioId(rs.getInt("usuario_id"));
                l.setLivroTitulo(rs.getString("livro_titulo"));
                l.setPaginasLidas(rs.getInt("paginas_lidas"));
                l.setDataLeitura(rs.getString("data_leitura"));
                lista.add(l);
            }
        }
        return lista;
    }
}