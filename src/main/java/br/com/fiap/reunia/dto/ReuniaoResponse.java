package br.com.fiap.reunia.dto;

import br.com.fiap.reunia.model.Reuniao;
import br.com.fiap.reunia.model.ReuniaoCS;
import br.com.fiap.reunia.model.ReuniaoVendas;
import br.com.fiap.reunia.model.StatusReuniao;
import br.com.fiap.reunia.model.TipoReuniao;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reuniao exposta pela API ja com os indicadores calculados pelas regras de negocio.
 * Campos que nao se aplicam ao tipo da reuniao sao omitidos do JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReuniaoResponse(
        Long id,
        TipoReuniao tipo,
        String titulo,
        LocalDateTime dataHora,
        int duracaoMinutos,
        StatusReuniao status,
        boolean processada,
        Long usuarioId,
        Long clienteId,
        double prioridade,
        String nivelPrioridade,
        String resumo,
        BigDecimal valorOportunidade,
        BigDecimal valorPonderado,
        Integer scoreRisco,
        Boolean concorrente,
        String classificacaoRisco,
        String alertaConcorrencia,
        Integer nps,
        Integer reclamacoes,
        Boolean sinalChurn,
        String classificacaoNps,
        String planoRetencao) {

    public static ReuniaoResponse de(Reuniao r) {
        double prioridade = Math.round(r.calcularPrioridade() * 100.0) / 100.0;
        if (r instanceof ReuniaoVendas v) {
            return new ReuniaoResponse(r.getId(), r.getTipo(), r.getTitulo(), r.getDataHora(), r.getDuracaoMinutos(),
                    r.getStatus(), r.isProcessada(), r.getUsuarioId(), r.getClienteId(), prioridade,
                    r.classificarPrioridade(), r.analisarConteudo(),
                    v.getValorOportunidade(), v.calcularValorPonderado(), v.getScoreRisco(), v.isConcorrente(),
                    v.classificarRisco(), v.alertaConcorrencia(),
                    null, null, null, null, null);
        }
        ReuniaoCS cs = (ReuniaoCS) r;
        return new ReuniaoResponse(r.getId(), r.getTipo(), r.getTitulo(), r.getDataHora(), r.getDuracaoMinutos(),
                r.getStatus(), r.isProcessada(), r.getUsuarioId(), r.getClienteId(), prioridade,
                r.classificarPrioridade(), r.analisarConteudo(),
                null, null, null, null, null, null,
                cs.getNps(), cs.getReclamacoes(), cs.isSinalChurn(), cs.classificarNps(), cs.relatorioRetencao());
    }
}
