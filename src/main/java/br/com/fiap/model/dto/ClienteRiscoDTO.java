package br.com.fiap.model.dto;

// Linha da lista de clientes em risco de churn (cancelamento)
public record ClienteRiscoDTO(
        Long codigoCliente,
        String nomeCliente,
        Long codigoReuniao,
        String tituloReuniao,
        Integer riscoChurn,
        String sentimento,
        String recomendacao
) {
}
