package br.com.fiap.reunia.exception;

/**
 * Registro solicitado nao existe no banco (HTTP 404).
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, long id) {
        super(recurso + " com id " + id + " não encontrado(a).");
    }
}
