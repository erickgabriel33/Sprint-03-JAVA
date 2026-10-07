package br.com.fiap.reunia.dao;

import br.com.fiap.reunia.model.Cliente;
import br.com.fiap.reunia.model.PorteEmpresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CRUD da tabela T_REUNIA_CLIENTE.
 */
public class ClienteDAO extends BaseDAO {

    private static final String COLUNAS = "id_cliente, nm_cliente, nr_cnpj, ds_segmento, ds_porte, ds_email_contato, dt_cadastro";

    public ClienteDAO(Connection conexao) {
        super(conexao);
    }

    public Cliente inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO T_REUNIA_CLIENTE (nm_cliente, nr_cnpj, ds_segmento, ds_porte, ds_email_contato) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"id_cliente"})) {
            preencher(ps, cliente);
            cliente.setId(inserirRetornandoId(ps));
        }
        return buscarPorId(cliente.getId()).orElseThrow();
    }

    /** Lista todos os clientes; se {@code nome} for informado, filtra por trecho do nome (sem diferenciar maiusculas). */
    public List<Cliente> listar(String nome) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM T_REUNIA_CLIENTE WHERE (? IS NULL OR UPPER(nm_cliente) LIKE UPPER(?)) ORDER BY nm_cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, nome == null ? null : "%" + nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapear(rs));
                }
            }
        }
        return clientes;
    }

    public Optional<Cliente> buscarPorId(long id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM T_REUNIA_CLIENTE WHERE id_cliente = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public boolean existeCnpj(String cnpj, Long ignorarId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM T_REUNIA_CLIENTE WHERE nr_cnpj = ? AND id_cliente <> ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, cnpj);
            ps.setLong(2, ignorarId == null ? -1 : ignorarId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE T_REUNIA_CLIENTE SET nm_cliente = ?, nr_cnpj = ?, ds_segmento = ?, ds_porte = ?, ds_email_contato = ? WHERE id_cliente = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencher(ps, cliente);
            ps.setLong(6, cliente.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("DELETE FROM T_REUNIA_CLIENTE WHERE id_cliente = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void preencher(PreparedStatement ps, Cliente cliente) throws SQLException {
        ps.setString(1, cliente.getNome());
        ps.setString(2, cliente.getCnpj());
        ps.setString(3, cliente.getSegmento());
        ps.setString(4, cliente.getPorte().name());
        ps.setString(5, cliente.getEmailContato());
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getLong("id_cliente"),
                rs.getString("nm_cliente"),
                rs.getString("nr_cnpj"),
                rs.getString("ds_segmento"),
                PorteEmpresa.valueOf(rs.getString("ds_porte")),
                rs.getString("ds_email_contato"),
                deTimestamp(rs.getTimestamp("dt_cadastro")));
    }
}
