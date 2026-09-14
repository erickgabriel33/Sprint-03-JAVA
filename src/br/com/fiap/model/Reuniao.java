package br.com.fiap.model;

public abstract class Reuniao implements Analisavel {
    protected int id;
    protected String titulo;
    protected String data;
    protected int duracaoMinutos;
    protected boolean processada;
    protected Usuario responsavel;

    public Reuniao() {}

    public Reuniao(int id, String titulo, String data, int duracaoMinutos, boolean processada, Usuario responsavel) {
        this.id = id;
        this.titulo = titulo;
        this.data = data;
        this.duracaoMinutos = duracaoMinutos;
        this.processada = processada;
        this.responsavel = responsavel;
    }

    public abstract String tipoReuniao();
    public abstract double calcularPrioridade();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
    public boolean isProcessada() { return processada; }
    public void setProcessada(boolean processada) { this.processada = processada; }
    public Usuario getResponsavel() { return responsavel; }
    public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }

    @Override
    public boolean analiseDisponivel() {
        return this.processada;
    }
}