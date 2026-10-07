package br.com.fiap.reunia.model;

import java.time.LocalDateTime;

/**
 * Texto transcrito de uma reuniao (tabela T_REUNIA_TRANSCRICAO).
 */
public class Transcricao {

    private Long id;
    private Long reuniaoId;
    private String texto;
    private int numeroPalavras;
    private LocalDateTime dataEnvio;

    public Transcricao() {
    }

    public Transcricao(Long id, Long reuniaoId, String texto, int numeroPalavras, LocalDateTime dataEnvio) {
        this.id = id;
        this.reuniaoId = reuniaoId;
        this.texto = texto;
        this.numeroPalavras = numeroPalavras;
        this.dataEnvio = dataEnvio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReuniaoId() { return reuniaoId; }
    public void setReuniaoId(Long reuniaoId) { this.reuniaoId = reuniaoId; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public int getNumeroPalavras() { return numeroPalavras; }
    public void setNumeroPalavras(int numeroPalavras) { this.numeroPalavras = numeroPalavras; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
}
