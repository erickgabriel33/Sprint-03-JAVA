package br.com.fiap.reunia.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Indicadores gerados automaticamente a partir da transcricao (tabela T_REUNIA_ANALISE).
 */
public class AnaliseReuniao {

    private Long id;
    private Long reuniaoId;
    private Sentimento sentimento;
    private int scoreSentimento;
    private int riscoChurn;
    private int scoreOportunidade;
    private List<String> produtos = new ArrayList<>();
    private List<String> concorrentes = new ArrayList<>();
    private List<String> termos = new ArrayList<>();
    private String recomendacao;
    private LocalDateTime dataAnalise;

    public String getNivelRiscoChurn() {
        if (riscoChurn >= 70) return "ALTO";
        if (riscoChurn >= 40) return "MEDIO";
        return "BAIXO";
    }

    public String getNivelOportunidade() {
        if (scoreOportunidade >= 60) return "ALTA";
        if (scoreOportunidade >= 30) return "MEDIA";
        return "BAIXA";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReuniaoId() { return reuniaoId; }
    public void setReuniaoId(Long reuniaoId) { this.reuniaoId = reuniaoId; }
    public Sentimento getSentimento() { return sentimento; }
    public void setSentimento(Sentimento sentimento) { this.sentimento = sentimento; }
    public int getScoreSentimento() { return scoreSentimento; }
    public void setScoreSentimento(int scoreSentimento) { this.scoreSentimento = scoreSentimento; }
    public int getRiscoChurn() { return riscoChurn; }
    public void setRiscoChurn(int riscoChurn) { this.riscoChurn = riscoChurn; }
    public int getScoreOportunidade() { return scoreOportunidade; }
    public void setScoreOportunidade(int scoreOportunidade) { this.scoreOportunidade = scoreOportunidade; }
    public List<String> getProdutos() { return produtos; }
    public void setProdutos(List<String> produtos) { this.produtos = produtos; }
    public List<String> getConcorrentes() { return concorrentes; }
    public void setConcorrentes(List<String> concorrentes) { this.concorrentes = concorrentes; }
    public List<String> getTermos() { return termos; }
    public void setTermos(List<String> termos) { this.termos = termos; }
    public String getRecomendacao() { return recomendacao; }
    public void setRecomendacao(String recomendacao) { this.recomendacao = recomendacao; }
    public LocalDateTime getDataAnalise() { return dataAnalise; }
    public void setDataAnalise(LocalDateTime dataAnalise) { this.dataAnalise = dataAnalise; }
}
