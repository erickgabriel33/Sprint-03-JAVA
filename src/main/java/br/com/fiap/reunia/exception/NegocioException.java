package br.com.fiap.reunia.exception;

/**
 * Violacao de regra de negocio ou dado de entrada invalido (HTTP 400).
 */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
