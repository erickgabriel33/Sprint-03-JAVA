package br.com.fiap.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "Transcricao")
@Table(name = "reunia_transcricoes")
public class Transcricao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;
    private Long codigoReuniao;
    @Column(length = 4000) // o padrao de uma coluna String e 255 caracteres, pouco para uma transcricao
    private String texto;
    private Integer quantidadePalavras;
    private LocalDateTime dataEnvio;

    public Transcricao() {
    }

    public Transcricao(Long codigoReuniao, String texto) {
        this.codigoReuniao = codigoReuniao;
        this.texto = texto;
        this.quantidadePalavras = texto.trim().split("\\s+").length;
        this.dataEnvio = LocalDateTime.now();
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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getQuantidadePalavras() {
        return quantidadePalavras;
    }

    public void setQuantidadePalavras(Integer quantidadePalavras) {
        this.quantidadePalavras = quantidadePalavras;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}
