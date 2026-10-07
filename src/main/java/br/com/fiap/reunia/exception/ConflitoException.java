package br.com.fiap.reunia.exception;

/**
 * Operacao gera duplicidade ou viola integridade referencial (HTTP 409).
 */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
