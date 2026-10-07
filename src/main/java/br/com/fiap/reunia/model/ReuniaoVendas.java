package br.com.fiap.reunia.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Reuniao de prospeccao/negociacao. Foco: valor da oportunidade, risco de perda e concorrencia.
 */
public class ReuniaoVendas extends Reuniao {

    private BigDecimal valorOportunidade = BigDecimal.ZERO;
    private int scoreRisco;
    private boolean concorrente;

    @Override
    public TipoReuniao getTipo() {
        return TipoReuniao.VENDAS;
    }

    /** Prioridade ponderada: valor (ate 50 pts) + risco (ate 30 pts) + concorrente (20 pts). */
    @Override
    public double calcularPrioridade() {
        double pesoValor = Math.min(valorOportunidade.doubleValue() / 10_000.0, 50.0);
        double pesoRisco = scoreRisco * 0.3;
        double pesoConcorrente = concorrente ? 20.0 : 0.0;
        return Math.min(pesoValor + pesoRisco + pesoConcorrente, 100.0);
    }

    /** Valor esperado do negocio considerando a chance de perda (valor x (1 - risco)). */
    public BigDecimal calcularValorPonderado() {
        BigDecimal chanceGanho = BigDecimal.valueOf(100 - scoreRisco).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return valorOportunidade.multiply(chanceGanho).setScale(2, RoundingMode.HALF_UP);
    }

    public String classificarRisco() {
        if (scoreRisco >= 75) return "RISCO CRÍTICO: Alta probabilidade de perda da negociação.";
        if (scoreRisco >= 40) return "RISCO MODERADO: Requer alinhamento de proposta.";
        return "RISCO BAIXO: Negociação sob controle.";
    }

    public String alertaConcorrencia() {
        if (concorrente && valorOportunidade.compareTo(BigDecimal.valueOf(50_000)) >= 0) {
            return "URGENTE: Presença direta de concorrente em conta estratégica (deal alto).";
        }
        if (concorrente) {
            return "ALERTA: Menção de concorrente durante a reunião.";
        }
        return "Sem menções a concorrentes.";
    }

    /** O risco passa a ser a media entre o risco informado e o risco detectado; concorrente citado liga o alerta. */
    @Override
    public void aplicarAnalise(AnaliseReuniao analise) {
        this.scoreRisco = Math.round((scoreRisco + analise.getRiscoChurn()) / 2.0f);
        if (!analise.getConcorrentes().isEmpty()) {
            this.concorrente = true;
        }
    }

    @Override
    public String analisarConteudo() {
        if (!analiseDisponivel()) return "Pendente de transcrição da IA.";
        return "Prioridade " + classificarPrioridade() + " | " + classificarRisco() + " | " + alertaConcorrencia();
    }

    public BigDecimal getValorOportunidade() { return valorOportunidade; }
    public void setValorOportunidade(BigDecimal valorOportunidade) { this.valorOportunidade = valorOportunidade; }
    public int getScoreRisco() { return scoreRisco; }
    public void setScoreRisco(int scoreRisco) { this.scoreRisco = scoreRisco; }
    public boolean isConcorrente() { return concorrente; }
    public void setConcorrente(boolean concorrente) { this.concorrente = concorrente; }
}
