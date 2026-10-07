package br.com.fiap.reunia.exception;

/**
 * Credenciais de login invalidas (HTTP 401).
 */
public class AutenticacaoException extends RuntimeException {

    public AutenticacaoException(String mensagem) {
        super(mensagem);
    }
}
