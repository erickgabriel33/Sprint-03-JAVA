package br.com.fiap.reunia.dao;

import br.com.fiap.reunia.model.Transcricao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Persistencia da tabela T_REUNIA_TRANSCRICAO (uma transcricao por reuniao).
 */
public class TranscricaoDAO extends BaseDAO {

    public TranscricaoDAO(Connection conexao) {
        super(conexao);
    }

    /** Insere a transcricao ou substitui a existente da mesma reuniao. */
    public Transcricao salvar(Transcricao transcricao) throws SQLException {
        Optional<Transcricao> existente = buscarPorReuniao(transcricao.getReuniaoId());
        if (existente.isPresent()) {
            String sql = "UPDATE T_REUNIA_TRANSCRICAO SET ds_texto = ?, nr_palavras = ?, dt_envio = SYSTIMESTAMP WHERE id_reuniao = ?";
            try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                ps.setString(1, transcricao.getTexto());
                ps.setInt(2, transcricao.getNumeroPalavras());
                ps.setLong(3, transcricao.getReuniaoId());
                ps.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO T_REUNIA_TRANSCRICAO (id_reuniao, ds_texto, nr_palavras) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"id_transcricao"})) {
                ps.setLong(1, transcricao.getReuniaoId());
                ps.setString(2, transcricao.getTexto());
                ps.setInt(3, transcricao.getNumeroPalavras());
                inserirRetornandoId(ps);
            }
        }
        return buscarPorReuniao(transcricao.getReuniaoId()).orElseThrow();
    }

    public Optional<Transcricao> buscarPorReuniao(long reuniaoId) throws SQLException {
        String sql = "SELECT id_transcricao, id_reuniao, ds_texto, nr_palavras, dt_envio FROM T_REUNIA_TRANSCRICAO WHERE id_reuniao = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setLong(1, reuniaoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Transcricao(
                        rs.getLong("id_transcricao"),
                        rs.getLong("id_reuniao"),
                        rs.getString("ds_texto"),
                        rs.getInt("nr_palavras"),
                        deTimestamp(rs.getTimestamp("dt_envio"))));
            }
        }
    }
}
