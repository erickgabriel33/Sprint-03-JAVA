package br.com.fiap.model;

public class ReuniaoVendas extends Reuniao {
    private String cliente;
    private double valorOportunidade;
    private int scoreRisco;
    private boolean concorrente;

    public ReuniaoVendas() {}

    public ReuniaoVendas(int id, String titulo, String data, int duracaoMinutos, boolean processada,
                         Usuario responsavel, String cliente, double valorOportunidade, int scoreRisco, boolean concorrente) {
        super(id, titulo, data, duracaoMinutos, processada, responsavel);
        this.cliente = cliente;
        this.valorOportunidade = valorOportunidade;
        this.scoreRisco = scoreRisco;
        this.concorrente = concorrente;
    }

    @Override
    public String tipoReuniao() {
        return "Vendas";
    }

    // Regra de Negócio: Cálculo de Prioridade Ponderada
    @Override
    public double calcularPrioridade() {
        double pesoValor = Math.min(this.valorOportunidade / 10000.0, 50.0);
        double pesoRisco = this.scoreRisco * 0.3;
        double pesoConcorrente = this.concorrente ? 20.0 : 0.0;
        return pesoValor + pesoRisco + pesoConcorrente;
    }

    // Regra de Negócio: Classificação de Risco
    public String classificarRisco() {
        if (this.scoreRisco >= 75) return "RISCO CRÍTICO: Alta probabilidade de perda da negociação.";
        if (this.scoreRisco >= 40) return "RISCO MODERADO: Requer alinhamento de proposta.";
        return "RISCO BAIXO: Negociação sob controle.";
    }

    // Regra de Negócio: Blindagem de Concorrência
    public String alertaConcorrencia() {
        if (this.concorrente && this.valorOportunidade >= 50000.0) {
            return "URGENTE: Presença direta de concorrente em conta estratégica (Deal Alto).";
        } else if (this.concorrente) {
            return "ALERTA: Menção de concorrente durante a reunião.";
        }
        return "Sem menções a concorrentes.";
    }

    @Override
    public String analisarConteudo() {
        if (!analiseDisponivel()) return "Pendente de transcrição da IA.";
        return "Cliente: " + cliente + " | Score Prioridade: " + String.format("%.2f", calcularPrioridade()) + " | Risco: " + classificarRisco();
    }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public double getValorOportunidade() { return valorOportunidade; }
    public void setValorOportunidade(double valorOportunidade) { this.valorOportunidade = valorOportunidade; }
    public int getScoreRisco() { return scoreRisco; }
    public void setScoreRisco(int scoreRisco) { this.scoreRisco = scoreRisco; }
    public boolean isConcorrente() { return concorrente; }
    public void setConcorrente(boolean concorrente) { this.concorrente = concorrente; }
}