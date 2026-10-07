package br.com.fiap.reunia.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Indicadores consolidados para a tela inicial (dashboard) do Front-End.
 */
public record DashboardResponse(
        int totalClientes,
        int totalReunioes,
        long reunioesProcessadas,
        Map<String, Long> reunioesPorStatus,
        Map<String, Long> reunioesPorTipo,
        Map<String, Long> sentimentos,
        BigDecimal valorPipeline,
        BigDecimal valorPipelinePonderado,
        double npsMedio,
        long reunioesAltaPrioridade,
        long clientesEmRisco) {
}
