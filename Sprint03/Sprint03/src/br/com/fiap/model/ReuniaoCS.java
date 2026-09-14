package br.com.fiap.model;

public class ReuniaoCS extends Reuniao {
    private String cliente;
    private int nps;
    private int reclamacoesTotal;
    private boolean sinalChurn;

    public ReuniaoCS() {}

    public ReuniaoCS(int id, String titulo, String data, int duracaoMinutos, boolean processada,
                     Usuario responsavel, String cliente, int nps, int reclamacoesTotal, boolean sinalChurn) {
        super(id, titulo, data, duracaoMinutos, processada, responsavel);
        this.cliente = cliente;
        this.nps = nps;
        this.reclamacoesTotal = reclamacoesTotal;
        this.sinalChurn = sinalChurn;
    }

    @Override
    public String tipoReuniao() {
        return "Customer Success (CS)";
    }

    @Override
    public double calcularPrioridade() {
        double prioridade = (10 - this.nps) * 5.0 + (this.reclamacoesTotal * 8.0);
        if (this.sinalChurn) {
            prioridade += 30.0;
        }
        return Math.min(prioridade, 100.0);
    }

    // Regra de Negócio: Classificação NPS
    public String classificarNPS() {
        if (this.nps >= 9) return "PROMOTOR: Cliente promotor e satisfeito.";
        if (this.nps >= 7) return "NEUTRO: Cliente passivo com potencial risco.";
        return "DETRATOR: Cliente insatisfeito. Ação imediata requerida.";
    }

    // Regra de Negócio: Plano de Ação para Retenção
    public String relatorioRetencao() {
        if (this.sinalChurn || this.reclamacoesTotal > 3) {
            return "PLANO DE RESGATE: Alto risco de churn. Squad de CS acionada.";
        }
        return "CONTA ESTÁVEL: Manter acompanhamento periódico.";
    }

    @Override
    public String analisarConteudo() {
        if (!analiseDisponivel()) return "Pendente de transcrição da IA.";
        return "Cliente: " + cliente + " | Status NPS: " + classificarNPS() + " | Retenção: " + relatorioRetencao();
    }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public int getNps() { return nps; }
    public void setNps(int nps) { this.nps = nps; }
    public int getReclamacoesTotal() { return reclamacoesTotal; }
    public void setReclamacoesTotal(int reclamacoesTotal) { this.reclamacoesTotal = reclamacoesTotal; }
    public boolean isSinalChurn() { return sinalChurn; }
    public void setSinalChurn(boolean sinalChurn) { this.sinalChurn = sinalChurn; }
}