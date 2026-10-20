package br.com.fiap.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Resultado da analise automatica de uma transcricao
@Entity(name = "Analise")
@Table(name = "reunia_analises")
public class Analise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;
    private Long codigoReuniao;
    private String sentimento;
    private Integer scoreSentimento;
    private Integer riscoChurn;
    private Integer scoreOportunidade;
    private String produtos;
    private String concorrentes;
    private String termos;
    @Column(length = 1000)
    private String recomendacao;
    private LocalDateTime dataAnalise;

    public Analise() {
    }

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

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodigoReuniao() {
        return codigoReuniao;
    }

    public void setCodigoReuniao(Long codigoReuniao) {
        this.codigoReuniao = codigoReuniao;
    }

    public String getSentimento() {
        return sentimento;
    }

    public void setSentimento(String sentimento) {
        this.sentimento = sentimento;
    }

    public Integer getScoreSentimento() {
        return scoreSentimento;
    }

    public void setScoreSentimento(Integer scoreSentimento) {
        this.scoreSentimento = scoreSentimento;
    }

    public Integer getRiscoChurn() {
        return riscoChurn;
    }

    public void setRiscoChurn(Integer riscoChurn) {
        this.riscoChurn = riscoChurn;
    }

    public Integer getScoreOportunidade() {
        return scoreOportunidade;
    }

    public void setScoreOportunidade(Integer scoreOportunidade) {
        this.scoreOportunidade = scoreOportunidade;
    }

    public String getProdutos() {
        return produtos;
    }

    public void setProdutos(String produtos) {
        this.produtos = produtos;
    }

    public String getConcorrentes() {
        return concorrentes;
    }

    public void setConcorrentes(String concorrentes) {
        this.concorrentes = concorrentes;
    }

    public String getTermos() {
        return termos;
    }

    public void setTermos(String termos) {
        this.termos = termos;
    }

    public String getRecomendacao() {
        return recomendacao;
    }

    public void setRecomendacao(String recomendacao) {
        this.recomendacao = recomendacao;
    }

    public LocalDateTime getDataAnalise() {
        return dataAnalise;
    }

    public void setDataAnalise(LocalDateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }
}
