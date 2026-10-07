package br.com.fiap.reunia.dto;

import br.com.fiap.reunia.model.Sentimento;

/**
 * Linha do painel "clientes em risco": reuniao analisada com risco de churn elevado.
 */
public record ClienteRiscoResponse(
        Long clienteId,
        String nomeCliente,
        Long reuniaoId,
        String tituloReuniao,
        int riscoChurn,
        Sentimento sentimento,
        String recomendacao) {
}
