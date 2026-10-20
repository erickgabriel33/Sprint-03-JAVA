package br.com.fiap.model.dto;

// Indicadores gerais exibidos na tela inicial
public record DashboardDTO(
        long totalClientes,
        long totalReunioes,
        long reunioesAgendadas,
        long reunioesRealizadas,
        long reunioesAltaPrioridade,
        long clientesEmRisco,
        double valorEmNegociacao,
        double npsMedio
) {
}
