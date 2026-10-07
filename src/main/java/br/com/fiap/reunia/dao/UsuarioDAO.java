package br.com.fiap.reunia.dao;

import br.com.fiap.reunia.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CRUD da tabela T_REUNIA_USUARIO.
 */
public class UsuarioDAO extends BaseDAO {

    private static final String COLUNAS = "id_usuario, nm_usuario, ds_email, ds_senha, ds_cargo, nm_empresa, dt_cadastro";

    public UsuarioDAO(Connection conexao) {
        super(conexao);
    }

    public Usuario inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO T_REUNIA_USUARIO (nm_usuario, ds_email, ds_senha, ds_cargo, nm_empresa) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"id_usuario"})) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenhaHash());
            ps.setString(4, usuario.getCargo());
            ps.setString(5, usuario.getEmpresa());
            usuario.setId(inserirRetornandoId(ps));
        }
        return buscarPorId(usuario.getId()).orElseThrow();
    }

    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM T_REUNIA_USUARIO ORDER BY nm_usuario";
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    public Optional<Usuario> buscarPorId(long id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM T_REUNIA_USUARIO WHERE id_usuario = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM T_REUNIA_USUARIO WHERE LOWER(ds_email) = LOWER(?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    /** Verifica duplicidade de e-mail ignorando o proprio registro (usado na atualizacao). */
    public boolean existeEmail(String email, Long ignorarId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM T_REUNIA_USUARIO WHERE LOWER(ds_email) = LOWER(?) AND id_usuario <> ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setLong(2, ignorarId == null ? -1 : ignorarId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE T_REUNIA_USUARIO SET nm_usuario = ?, ds_email = ?, ds_senha = ?, ds_cargo = ?, nm_empresa = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenhaHash());
            ps.setString(4, usuario.getCargo());
            ps.setString(5, usuario.getEmpresa());
            ps.setLong(6, usuario.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("DELETE FROM T_REUNIA_USUARIO WHERE id_usuario = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getLong("id_usuario"),
                rs.getString("nm_usuario"),
                rs.getString("ds_email"),
                rs.getString("ds_senha"),
                rs.getString("ds_cargo"),
                rs.getString("nm_empresa"),
                deTimestamp(rs.getTimestamp("dt_cadastro")));
    }
}
