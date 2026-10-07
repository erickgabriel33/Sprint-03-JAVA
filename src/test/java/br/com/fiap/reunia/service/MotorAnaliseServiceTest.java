package br.com.fiap.reunia.service;

import br.com.fiap.reunia.model.AnaliseReuniao;
import br.com.fiap.reunia.model.Sentimento;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotorAnaliseServiceTest {

    private final MotorAnaliseService motor = new MotorAnaliseService();

    @Test
    void detectaRiscoDeChurnEmClienteInsatisfeito() {
        AnaliseReuniao analise = motor.analisar("Cliente insatisfeito com a lentidão do RM e com o suporte. "
                + "Relatou vários problemas e disse que está avaliando cancelar o contrato e migrar para outro sistema.");

        assertEquals(Sentimento.NEGATIVO, analise.getSentimento());
        assertTrue(analise.getRiscoChurn() >= 70, "risco deveria ser alto: " + analise.getRiscoChurn());
        assertEquals(List.of("RM"), analise.getProdutos());
        assertTrue(analise.getRecomendacao().contains("Risco crítico de churn"));
    }

    @Test
    void detectaOportunidadeEConcorrente() {
        AnaliseReuniao analise = motor.analisar("Cliente gostou da demonstração do módulo de logística do Protheus, "
                + "mas a SAP ofereceu desconto. Pediu uma proposta revisada e orçamento para expandir as licenças.");

        assertEquals(List.of("Protheus"), analise.getProdutos());
        assertEquals(List.of("SAP"), analise.getConcorrentes());
        assertTrue(analise.getScoreOportunidade() >= 60, "oportunidade deveria ser alta: " + analise.getScoreOportunidade());
        assertTrue(analise.getRecomendacao().contains("Concorrente citado (SAP)"));
    }

    @Test
    void negacaoInverteOSentimento() {
        AnaliseReuniao analise = motor.analisar("O time não ficou satisfeito e não gostou da nova versão apresentada.");

        assertEquals(Sentimento.NEGATIVO, analise.getSentimento());
    }

    @Test
    void textoNeutroGeraRecomendacaoPadrao() {
        AnaliseReuniao analise = motor.analisar("Reunião de alinhamento sobre o cronograma do próximo trimestre.");

        assertEquals(Sentimento.NEUTRO, analise.getSentimento());
        assertEquals(0, analise.getRiscoChurn());
        assertEquals("Sem sinais relevantes: manter acompanhamento periódico.", analise.getRecomendacao());
    }

    @Test
    void retornaNoMaximoCincoTermos() {
        AnaliseReuniao analise = motor.analisar("fluig fluig fluig workflow workflow portal intranet processos "
                + "aprovacao documentos assinatura");

        assertEquals(5, analise.getTermos().size());
        assertEquals("fluig", analise.getTermos().getFirst());
    }
}
