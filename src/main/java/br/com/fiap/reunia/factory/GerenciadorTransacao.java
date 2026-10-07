package br.com.fiap.reunia.factory;

import br.com.fiap.reunia.exception.BancoDeDadosException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abre a conexao, executa a operacao e garante commit/rollback e fechamento da conexao.
 * Os Services usam esta classe para que varias chamadas de DAO formem uma unica transacao.
 */
public final class GerenciadorTransacao {

    @FunctionalInterface
    public interface OperacaoBanco<T> {
        T executar(Connection conexao) throws SQLException;
    }

    private GerenciadorTransacao() {
    }

    /** Somente leitura: nao abre transacao. */
    public static <T> T consultar(OperacaoBanco<T> operacao) {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            return operacao.executar(conexao);
        } catch (SQLException e) {
            throw new BancoDeDadosException("Falha ao consultar o banco de dados.", e);
        }
    }

    /** Escrita: commit se tudo der certo, rollback em qualquer excecao (inclusive de regra de negocio). */
    public static <T> T executarEmTransacao(OperacaoBanco<T> operacao) {
        try (Connection conexao = ConnectionFactory.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                T resultado = operacao.executar(conexao);
                conexao.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new BancoDeDadosException("Falha ao gravar no banco de dados.", e);
        }
    }
}
