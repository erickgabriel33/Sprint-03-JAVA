package br.com.fiap.reunia.model;

/**
 * Reuniao de Customer Success. Foco: satisfacao (NPS), reclamacoes e risco de cancelamento (churn).
 */
public class ReuniaoCS extends Reuniao {

    private int nps;
    private int reclamacoes;
    private boolean sinalChurn;

    @Override
    public TipoReuniao getTipo() {
        return TipoReuniao.CS;
    }

    /** NPS baixo, reclamacoes e sinal de churn elevam a prioridade (maximo 100). */
    @Override
    public double calcularPrioridade() {
        double prioridade = (10 - nps) * 5.0 + reclamacoes * 8.0;
        if (sinalChurn) {
            prioridade += 30.0;
        }
        return Math.min(prioridade, 100.0);
    }

    public String classificarNps() {
        if (nps >= 9) return "PROMOTOR: Cliente promotor e satisfeito.";
        if (nps >= 7) return "NEUTRO: Cliente passivo com potencial risco.";
        return "DETRATOR: Cliente insatisfeito. Ação imediata requerida.";
    }

    public String relatorioRetencao() {
        if (sinalChurn || reclamacoes > 3) {
            return "PLANO DE RESGATE: Alto risco de churn. Squad de CS acionada.";
        }
        return "CONTA ESTÁVEL: Manter acompanhamento periódico.";
    }

    /** Risco de churn detectado a partir de 60 marca o sinal de churn da conta. */
    @Override
    public void aplicarAnalise(AnaliseReuniao analise) {
        if (analise.getRiscoChurn() >= 60) {
            this.sinalChurn = true;
        }
    }

    @Override
    public String analisarConteudo() {
        if (!analiseDisponivel()) return "Pendente de transcrição da IA.";
        return "Prioridade " + classificarPrioridade() + " | " + classificarNps() + " | " + relatorioRetencao();
    }

    public int getNps() { return nps; }
    public void setNps(int nps) { this.nps = nps; }
    public int getReclamacoes() { return reclamacoes; }
    public void setReclamacoes(int reclamacoes) { this.reclamacoes = reclamacoes; }
    public boolean isSinalChurn() { return sinalChurn; }
    public void setSinalChurn(boolean sinalChurn) { this.sinalChurn = sinalChurn; }
}
