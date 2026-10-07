package br.com.fiap.reunia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Base dos DAOs: recebe a conexao do Service (que controla a transacao) e concentra conversoes Oracle <-> Java.
 */
public abstract class BaseDAO {

    protected final Connection conexao;

    protected BaseDAO(Connection conexao) {
        this.conexao = conexao;
    }

    /** Executa um INSERT e devolve o id gerado pela coluna IDENTITY informada. */
    protected long inserirRetornandoId(PreparedStatement ps) throws SQLException {
        ps.executeUpdate();
        try (ResultSet chaves = ps.getGeneratedKeys()) {
            if (chaves.next()) {
                return chaves.getLong(1);
            }
        }
        throw new SQLException("O banco não retornou o id gerado.");
    }

    protected static String paraFlag(boolean valor) {
        return valor ? "S" : "N";
    }

    protected static boolean deFlag(String valor) {
        return "S".equals(valor);
    }

    protected static Timestamp paraTimestamp(LocalDateTime data) {
        return data == null ? null : Timestamp.valueOf(data);
    }

    protected static LocalDateTime deTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
