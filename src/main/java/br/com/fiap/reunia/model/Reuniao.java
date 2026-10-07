package br.com.fiap.reunia.model;

import br.com.fiap.reunia.exception.NegocioException;

import java.time.LocalDateTime;

/**
 * Reuniao comercial (tabela T_REUNIA_REUNIAO). Especializada em {@link ReuniaoVendas} e {@link ReuniaoCS}.
 */
public abstract class Reuniao implements Analisavel {

    protected Long id;
    protected String titulo;
    protected LocalDateTime dataHora;
    protected int duracaoMinutos;
    protected StatusReuniao status = StatusReuniao.AGENDADA;
    protected boolean processada;
    protected Long usuarioId;
    protected Long clienteId;

    public abstract TipoReuniao getTipo();

    /** Score de 0 a 100 que ordena a fila de atendimento da area comercial. */
    public abstract double calcularPrioridade();

    /** Atualiza os indicadores da reuniao com o resultado da analise automatica da transcricao. */
    public abstract void aplicarAnalise(AnaliseReuniao analise);

    public String classificarPrioridade() {
        double prioridade = calcularPrioridade();
        if (prioridade >= 70) return "ALTA";
        if (prioridade >= 40) return "MEDIA";
        return "BAIXA";
    }

    /** Regra de negocio: valida a transicao de status antes de aplica-la. */
    public void alterarStatus(StatusReuniao novoStatus) {
        if (novoStatus == null) {
            throw new NegocioException("Informe o novo status da reunião.");
        }
        if (!status.podeMudarPara(novoStatus)) {
            throw new NegocioException("Não é permitido alterar o status de " + status + " para " + novoStatus + ".");
        }
        this.status = novoStatus;
    }

    /** Regra de negocio: reunioes canceladas nao recebem transcricao; ao receber, a reuniao passa a REALIZADA. */
    public void registrarTranscricao() {
        if (status == StatusReuniao.CANCELADA) {
            throw new NegocioException("Não é possível enviar transcrição de uma reunião cancelada.");
        }
        this.status = StatusReuniao.REALIZADA;
        this.processada = true;
    }

    @Override
    public boolean analiseDisponivel() {
        return processada;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
    public StatusReuniao getStatus() { return status; }
    public void setStatus(StatusReuniao status) { this.status = status; }
    public boolean isProcessada() { return processada; }
    public void setProcessada(boolean processada) { this.processada = processada; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    @Override
    public String toString() {
        return getTipo() + "[id=" + id + ", titulo=" + titulo + ", status=" + status + "]";
    }
}
