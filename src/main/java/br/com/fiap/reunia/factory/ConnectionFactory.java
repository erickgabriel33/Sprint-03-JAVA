package br.com.fiap.reunia.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fabrica de conexoes JDBC com o Oracle da FIAP (padrao Factory).
 * O driver ojdbc11 e baixado automaticamente pelo Maven (ver pom.xml).
 *
 * ATENCAO: troque USUARIO e SENHA pelo seu RM e senha do Oracle da FIAP antes de rodar.
 */
public final class ConnectionFactory {

    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    private static final String USUARIO = "SEU_RM";
    private static final String SENHA = "SUA_SENHA";

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do Oracle não encontrado. Execute 'mvn clean install' para baixá-lo.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
