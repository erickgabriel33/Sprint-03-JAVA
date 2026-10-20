package br.com.fiap.model.entity;

import br.com.fiap.model.dto.ReuniaoDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;

// Reuniao de VENDAS (usa valorOportunidade, scoreRisco e concorrente)
// ou de CS - Customer Success (usa nps, reclamacoes e sinalChurn)
@Entity(name = "Reuniao")
@Table(name = "reunia_reunioes")
public class Reuniao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;
    private String titulo;
    private LocalDateTime dataHora;
    private Integer duracaoMinutos;
    private String tipo;
    private String status;
    private Boolean processada;
    private Long codigoUsuario;
    private Long codigoCliente;
    private Double valorOportunidade;
    private Integer scoreRisco;
    private Boolean concorrente;
    private Integer nps;
    private Integer reclamacoes;
    private Boolean sinalChurn;

    public Reuniao() {
    }

    public Reuniao(ReuniaoDTO reuniaoDTO) {
        this.titulo = reuniaoDTO.titulo();
        this.dataHora = reuniaoDTO.dataHora();
        this.duracaoMinutos = reuniaoDTO.duracaoMinutos();
        this.tipo = reuniaoDTO.tipo();
        this.codigoUsuario = reuniaoDTO.codigoUsuario();
        this.codigoCliente = reuniaoDTO.codigoCliente();
        this.valorOportunidade = reuniaoDTO.valorOportunidade();
        this.scoreRisco = reuniaoDTO.scoreRisco();
        this.concorrente = reuniaoDTO.concorrente();
        this.nps = reuniaoDTO.nps();
        this.reclamacoes = reuniaoDTO.reclamacoes();
        this.sinalChurn = reuniaoDTO.sinalChurn();
        this.status = "AGENDADA";
        this.processada = false;
    }

    // Regra de negocio: prioridade de atendimento de 0 a 100
    public double calcularPrioridade() {
        double prioridade;
        if ("VENDAS".equals(tipo)) {
            double pesoValor = Math.min(valorOportunidade / 10000.0, 50.0);
            double pesoRisco = scoreRisco * 0.3;
            double pesoConcorrente = concorrente ? 20.0 : 0.0;
            prioridade = pesoValor + pesoRisco + pesoConcorrente;
        } else {
            prioridade = (10 - nps) * 5.0 + reclamacoes * 8.0;
            if (sinalChurn) {
                prioridade += 30.0;
            }
        }
        return Math.min(prioridade, 100.0);
    }

    // Regra de negocio: classificacao de risco da negociacao (VENDAS) ou do NPS (CS)
    public String classificar() {
        if ("VENDAS".equals(tipo)) {
            if (scoreRisco >= 75) return "RISCO CRÍTICO: Alta probabilidade de perda da negociação.";
            if (scoreRisco >= 40) return "RISCO MODERADO: Requer alinhamento de proposta.";
            return "RISCO BAIXO: Negociação sob controle.";
        }
        if (nps >= 9) return "PROMOTOR: Cliente promotor e satisfeito.";
        if (nps >= 7) return "NEUTRO: Cliente passivo com potencial risco.";
        return "DETRATOR: Cliente insatisfeito. Ação imediata requerida.";
    }

    // Regra de negocio: alerta de concorrencia (VENDAS) ou plano de retencao (CS)
    public String gerarAlerta() {
        if ("VENDAS".equals(tipo)) {
            if (concorrente && valorOportunidade >= 50000.0) {
                return "URGENTE: Presença de concorrente em conta estratégica (deal alto).";
            }
            if (concorrente) {
                return "ALERTA: Menção de concorrente durante a reunião.";
            }
            return "Sem menções a concorrentes.";
        }
        if (sinalChurn || reclamacoes > 3) {
            return "PLANO DE RESGATE: Alto risco de churn. Squad de CS acionada.";
        }
        return "CONTA ESTÁVEL: Manter acompanhamento periódico.";
    }

    // Regra de negocio: AGENDADA -> REALIZADA ou CANCELADA; CANCELADA -> AGENDADA; REALIZADA e final
    public boolean podeMudarStatus(String novoStatus) {
        if ("AGENDADA".equals(status)) {
            return "REALIZADA".equals(novoStatus) || "CANCELADA".equals(novoStatus);
        }
        if ("CANCELADA".equals(status)) {
            return "AGENDADA".equals(novoStatus);
        }
        return false;
    }

    // Os getters abaixo nao sao colunas: enviam no JSON os resultados das regras de negocio
    public double getPrioridade() {
        return Math.round(calcularPrioridade() * 100.0) / 100.0;
    }

    public String getNivelPrioridade() {
        double prioridade = calcularPrioridade();
        if (prioridade >= 70) return "ALTA";
        if (prioridade >= 40) return "MEDIA";
        return "BAIXA";
    }

    public String getClassificacao() {
        return classificar();
    }

    public String getAlerta() {
        return gerarAlerta();
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getProcessada() {
        return processada;
    }

    public void setProcessada(Boolean processada) {
        this.processada = processada;
    }

    public Long getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(Long codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    public Long getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(Long codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Double getValorOportunidade() {
        return valorOportunidade;
    }

    public void setValorOportunidade(Double valorOportunidade) {
        this.valorOportunidade = valorOportunidade;
    }

    public Integer getScoreRisco() {
        return scoreRisco;
    }

    public void setScoreRisco(Integer scoreRisco) {
        this.scoreRisco = scoreRisco;
    }

    public Boolean getConcorrente() {
        return concorrente;
    }

    public void setConcorrente(Boolean concorrente) {
        this.concorrente = concorrente;
    }

    public Integer getNps() {
        return nps;
    }

    public void setNps(Integer nps) {
        this.nps = nps;
    }

    public Integer getReclamacoes() {
        return reclamacoes;
    }

    public void setReclamacoes(Integer reclamacoes) {
        this.reclamacoes = reclamacoes;
    }

    public Boolean getSinalChurn() {
        return sinalChurn;
    }

    public void setSinalChurn(Boolean sinalChurn) {
        this.sinalChurn = sinalChurn;
    }
}
