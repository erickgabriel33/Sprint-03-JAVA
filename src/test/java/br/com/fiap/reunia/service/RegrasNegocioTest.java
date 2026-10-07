package br.com.fiap.reunia.service;

import br.com.fiap.reunia.exception.NegocioException;
import br.com.fiap.reunia.model.AnaliseReuniao;
import br.com.fiap.reunia.model.ReuniaoCS;
import br.com.fiap.reunia.model.ReuniaoVendas;
import br.com.fiap.reunia.model.StatusReuniao;
import br.com.fiap.reunia.util.SenhaUtil;
import br.com.fiap.reunia.util.ValidadorUtil;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes das regras de negocio da camada Model e das validacoes usadas pelos Services.
 */
class RegrasNegocioTest {

    @Test
    void prioridadeDeVendasConsideraValorRiscoEConcorrente() {
        ReuniaoVendas vendas = new ReuniaoVendas();
        vendas.setValorOportunidade(new BigDecimal("85000"));
        vendas.setScoreRisco(80);
        vendas.setConcorrente(true);

        assertEquals(8.5 + 24 + 20, vendas.calcularPrioridade(), 0.001);
        assertEquals("MEDIA", vendas.classificarPrioridade());
        assertEquals(new BigDecimal("17000.00"), vendas.calcularValorPonderado());
        assertTrue(vendas.alertaConcorrencia().startsWith("URGENTE"));
    }

    @Test
    void prioridadeDeCsELimitadaA100() {
        ReuniaoCS cs = new ReuniaoCS();
        cs.setNps(2);
        cs.setReclamacoes(6);
        cs.setSinalChurn(true);

        assertEquals(100.0, cs.calcularPrioridade());
        assertTrue(cs.classificarNps().startsWith("DETRATOR"));
        assertTrue(cs.relatorioRetencao().startsWith("PLANO DE RESGATE"));
    }

    @Test
    void analiseAtualizaIndicadoresDaReuniao() {
        AnaliseReuniao analise = new AnaliseReuniao();
        analise.setRiscoChurn(70);
        analise.setConcorrentes(List.of("SAP"));

        ReuniaoVendas vendas = new ReuniaoVendas();
        vendas.setScoreRisco(30);
        vendas.aplicarAnalise(analise);
        assertEquals(50, vendas.getScoreRisco());
        assertTrue(vendas.isConcorrente());

        ReuniaoCS cs = new ReuniaoCS();
        cs.aplicarAnalise(analise);
        assertTrue(cs.isSinalChurn());
    }

    @Test
    void cicloDeStatusDaReuniao() {
        ReuniaoCS reuniao = new ReuniaoCS();
        reuniao.alterarStatus(StatusReuniao.CANCELADA);
        reuniao.alterarStatus(StatusReuniao.AGENDADA);
        reuniao.alterarStatus(StatusReuniao.REALIZADA);

        assertThrows(NegocioException.class, () -> reuniao.alterarStatus(StatusReuniao.CANCELADA));
    }

    @Test
    void reuniaoCanceladaNaoRecebeTranscricao() {
        ReuniaoVendas reuniao = new ReuniaoVendas();
        reuniao.setStatus(StatusReuniao.CANCELADA);

        assertThrows(NegocioException.class, reuniao::registrarTranscricao);
    }

    @Test
    void validaCnpj() {
        assertEquals("11222333000181", ValidadorUtil.cnpj("11.222.333/0001-81"));
        assertThrows(NegocioException.class, () -> ValidadorUtil.cnpj("11.222.333/0001-82"));
        assertThrows(NegocioException.class, () -> ValidadorUtil.cnpj("11111111111111"));
    }

    @Test
    void validaEmailESenha() {
        assertEquals("erick@totvs.com.br", ValidadorUtil.email(" Erick@TOTVS.com.br ", "email", true));
        assertThrows(NegocioException.class, () -> ValidadorUtil.email("erick.totvs.com", "email", true));
        assertThrows(NegocioException.class, () -> ValidadorUtil.senhaForte("abcdefgh"));
    }

    @Test
    void hashDeSenhaConfereSomenteComASenhaCorreta() {
        String hash = SenhaUtil.gerarHash("Totvs@2026");

        assertTrue(SenhaUtil.confere("Totvs@2026", hash));
        assertFalse(SenhaUtil.confere("outraSenha1", hash));
    }
}
