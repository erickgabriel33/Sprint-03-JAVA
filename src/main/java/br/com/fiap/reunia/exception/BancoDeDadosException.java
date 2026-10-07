package br.com.fiap.reunia.exception;

/**
 * Falha de infraestrutura ao acessar o Oracle (HTTP 500). Encapsula a SQLException original.
 */
public class BancoDeDadosException extends RuntimeException {

    public BancoDeDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
