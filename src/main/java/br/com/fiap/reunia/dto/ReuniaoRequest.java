package br.com.fiap.reunia.dto;

import br.com.fiap.reunia.model.TipoReuniao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Dados de cadastro/atualizacao de reuniao.
 * VENDAS usa valorOportunidade, scoreRisco e concorrente; CS usa nps, reclamacoes e sinalChurn.
 */
public record ReuniaoRequest(
        String titulo,
        LocalDateTime dataHora,
        Integer duracaoMinutos,
        TipoReuniao tipo,
        Long usuarioId,
        Long clienteId,
        BigDecimal valorOportunidade,
        Integer scoreRisco,
        Boolean concorrente,
        Integer nps,
        Integer reclamacoes,
        Boolean sinalChurn) {
}
