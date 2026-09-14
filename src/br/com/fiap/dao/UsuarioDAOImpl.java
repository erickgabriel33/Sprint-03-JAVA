package br.com.fiap.dao;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.factory.ConnectionFactory;
import br.com.fiap.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public void salvar(Usuario usuario) throws NegocioException {
        String sql = "INSERT INTO T_REUNIA_USUARIO (nm_usuario, ds_email, ds_cargo, nm_empresa) VALUES (?, ?, ?, ?)";
        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Inicia transação manual

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario.getNome());
                ps.setString(2, usuario.getEmail());
                ps.setString(3, usuario.getCargo());
                ps.setString(4, usuario.getEmpresa());
                ps.executeUpdate();
            }

            conn.commit(); // Efetiva transação
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new NegocioException("Falha ao reverter transação: " + ex.getMessage(), ex);
                }
            }
            throw new NegocioException("Erro ao cadastrar usuário no banco: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new NegocioException("Erro ao encerrar conexão: " + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public List<Usuario> listarTodos() throws NegocioException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nm_usuario, ds_email, ds_cargo, nm_empresa FROM T_REUNIA_USUARIO ORDER BY id_usuario";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nm_usuario"),
                        rs.getString("ds_email"),
                        rs.getString("ds_cargo"),
                        rs.getString("nm_empresa")
                );
                lista.add(u);
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao consultar usuários: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Usuario buscarPorId(int id) throws NegocioException {
        String sql = "SELECT id_usuario, nm_usuario, ds_email, ds_cargo, nm_empresa FROM T_REUNIA_USUARIO WHERE id_usuario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nm_usuario"),
                            rs.getString("ds_email"),
                            rs.getString("ds_cargo"),
                            rs.getString("nm_empresa")
                    );
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao localizar usuário por ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean atualizar(Usuario usuario) throws NegocioException {
        String sql = "UPDATE T_REUNIA_USUARIO SET nm_usuario = ?, ds_email = ?, ds_cargo = ?, nm_empresa = ? WHERE id_usuario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getCargo());
            ps.setString(4, usuario.getEmpresa());
            ps.setInt(5, usuario.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new NegocioException("Erro ao atualizar dados do usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean excluir(int id) throws NegocioException {
        String sql = "DELETE FROM T_REUNIA_USUARIO WHERE id_usuario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new NegocioException("Erro ao remover usuário: " + e.getMessage(), e);
        }
    }
}