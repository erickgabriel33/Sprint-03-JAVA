package br.com.fiap.exception;

// Lancada pelos services quando uma regra de negocio e violada (o controller responde 400 BAD REQUEST)
public class NegocioException extends Exception {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
