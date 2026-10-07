package br.com.fiap.reunia.exception;

import java.time.LocalDateTime;

/**
 * Corpo JSON padronizado devolvido em qualquer erro da API.
 */
public record ErroResponse(int status, String erro, String mensagem, LocalDateTime timestamp) {

    public static ErroResponse of(int status, String erro, String mensagem) {
        return new ErroResponse(status, erro, mensagem, LocalDateTime.now());
    }
}
