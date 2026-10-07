package br.com.fiap.reunia.dao;

import br.com.fiap.reunia.model.Reuniao;
import br.com.fiap.reunia.model.ReuniaoCS;
import br.com.fiap.reunia.model.ReuniaoVendas;
import br.com.fiap.reunia.model.StatusReuniao;
import br.com.fiap.reunia.model.TipoReuniao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CRUD da tabela T_REUNIA_REUNIAO. A coluna tp_reuniao decide qual subclasse de {@link Reuniao} e instanciada.
 */
public class ReuniaoDAO extends BaseDAO {

    private static final String COLUNAS = "id_reuniao, id_usuario, id_cliente, ds_titulo, dt_reuniao, nr_duracao_min, "
            + "tp_reuniao, st_reuniao, fl_processada, vl_oportunidade, nr_score_risco, fl_concorrente, "
            + "nr_nps, nr_reclamacoes, fl_sinal_churn";

    public ReuniaoDAO(Connection conexao) {
        super(conexao);
    }

    public Reuniao inserir(Reuniao reuniao) throws SQLException {
        String sql = "INSERT INTO T_REUNIA_REUNIAO (id_usuario, id_cliente, ds_titulo, dt_reuniao, nr_duracao_min, "
                + "tp_reuniao, st_reuniao, fl_processada, vl_oportunidade, nr_score_risco, fl_concorrente, "
                + "nr_nps, nr_reclamacoes, fl_sinal_churn) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"id_reuniao"})) {
            preencher(ps, reuniao);
            reuniao.setId(inserirRetornandoId(ps));
        }
        return reuniao;
    }

    /** Lista com filtros opcionais (parametros nulos sao ignorados). Mais recentes primeiro. */
    public List<Reuniao> listar(TipoReuniao tipo, StatusReuniao status, Long clienteId, Long usuarioId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + COLUNAS + " FROM T_REUNIA_REUNIAO WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (tipo != null) {
            sql.append(" AND tp_reuniao = ?");
            parametros.add(tipo.name());
        }
        if (status != null) {
            sql.append(" AND st_reuniao = ?");
            parametros.add(status.name());
        }
        if (clienteId != null) {
            sql.append(" AND id_cliente = ?");
            parametros.add(clienteId);
        }
        if (usuarioId != null) {
            sql.append(" AND id_usuario = ?");
            parametros.add(usuarioId);
        }
        sql.append(" ORDER BY dt_reuniao DESC");

        List<Reuniao> reunioes = new ArrayList<>();
        try (PreparedStatement ps = conexao.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reunioes.add(mapear(rs));
                }
            }
        }
        return reunioes;
    }

    public Optional<Reuniao> buscarPorId(long id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM T_REUNIA_REUNIAO WHERE id_reuniao = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public boolean atualizar(Reuniao reuniao) throws SQLException {
        String sql = "UPDATE T_REUNIA_REUNIAO SET id_usuario = ?, id_cliente = ?, ds_titulo = ?, dt_reuniao = ?, "
                + "nr_duracao_min = ?, tp_reuniao = ?, st_reuniao = ?, fl_processada = ?, vl_oportunidade = ?, "
                + "nr_score_risco = ?, fl_concorrente = ?, nr_nps = ?, nr_reclamacoes = ?, fl_sinal_churn = ? "
                + "WHERE id_reuniao = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            preencher(ps, reuniao);
            ps.setLong(15, reuniao.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement("DELETE FROM T_REUNIA_REUNIAO WHERE id_reuniao = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int contarPorCliente(long clienteId) throws SQLException {
        return contar("SELECT COUNT(*) FROM T_REUNIA_REUNIAO WHERE id_cliente = ?", clienteId);
    }

    public int contarPorUsuario(long usuarioId) throws SQLException {
        return contar("SELECT COUNT(*) FROM T_REUNIA_REUNIAO WHERE id_usuario = ?", usuarioId);
    }

    /** Regra de agenda: o mesmo cliente nao pode ter duas reunioes no mesmo horario. */
    public boolean existeConflitoAgenda(long clienteId, LocalDateTime dataHora, Long ignorarId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM T_REUNIA_REUNIAO WHERE id_cliente = ? AND dt_reuniao = ? AND id_reuniao <> ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            ps.setTimestamp(2, paraTimestamp(dataHora));
            ps.setLong(3, ignorarId == null ? -1 : ignorarId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private int contar(String sql, long id) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void preencher(PreparedStatement ps, Reuniao reuniao) throws SQLException {
        ps.setLong(1, reuniao.getUsuarioId());
        ps.setLong(2, reuniao.getClienteId());
        ps.setString(3, reuniao.getTitulo());
        ps.setTimestamp(4, paraTimestamp(reuniao.getDataHora()));
        ps.setInt(5, reuniao.getDuracaoMinutos());
        ps.setString(6, reuniao.getTipo().name());
        ps.setString(7, reuniao.getStatus().name());
        ps.setString(8, paraFlag(reuniao.isProcessada()));

        if (reuniao instanceof ReuniaoVendas vendas) {
            ps.setBigDecimal(9, vendas.getValorOportunidade());
            ps.setInt(10, vendas.getScoreRisco());
            ps.setString(11, paraFlag(vendas.isConcorrente()));
            ps.setNull(12, Types.NUMERIC);
            ps.setNull(13, Types.NUMERIC);
            ps.setNull(14, Types.CHAR);
        } else if (reuniao instanceof ReuniaoCS cs) {
            ps.setNull(9, Types.NUMERIC);
            ps.setNull(10, Types.NUMERIC);
            ps.setNull(11, Types.CHAR);
            ps.setInt(12, cs.getNps());
            ps.setInt(13, cs.getReclamacoes());
            ps.setString(14, paraFlag(cs.isSinalChurn()));
        }
    }

    private Reuniao mapear(ResultSet rs) throws SQLException {
        TipoReuniao tipo = TipoReuniao.valueOf(rs.getString("tp_reuniao"));
        Reuniao reuniao;
        if (tipo == TipoReuniao.VENDAS) {
            ReuniaoVendas vendas = new ReuniaoVendas();
            vendas.setValorOportunidade(rs.getBigDecimal("vl_oportunidade"));
            vendas.setScoreRisco(rs.getInt("nr_score_risco"));
            vendas.setConcorrente(deFlag(rs.getString("fl_concorrente")));
            reuniao = vendas;
        } else {
            ReuniaoCS cs = new ReuniaoCS();
            cs.setNps(rs.getInt("nr_nps"));
            cs.setReclamacoes(rs.getInt("nr_reclamacoes"));
            cs.setSinalChurn(deFlag(rs.getString("fl_sinal_churn")));
            reuniao = cs;
        }
        reuniao.setId(rs.getLong("id_reuniao"));
        reuniao.setUsuarioId(rs.getLong("id_usuario"));
        reuniao.setClienteId(rs.getLong("id_cliente"));
        reuniao.setTitulo(rs.getString("ds_titulo"));
        reuniao.setDataHora(deTimestamp(rs.getTimestamp("dt_reuniao")));
        reuniao.setDuracaoMinutos(rs.getInt("nr_duracao_min"));
        reuniao.setStatus(StatusReuniao.valueOf(rs.getString("st_reuniao")));
        reuniao.setProcessada(deFlag(rs.getString("fl_processada")));
        return reuniao;
    }
}
