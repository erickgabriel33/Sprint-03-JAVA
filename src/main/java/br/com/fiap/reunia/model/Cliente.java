package br.com.fiap.reunia.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Empresa cliente da TOTVS (tabela T_REUNIA_CLIENTE).
 */
public class Cliente {

    private Long id;
    private String nome;
    private String cnpj;
    private String segmento;
    private PorteEmpresa porte;
    private String emailContato;
    private LocalDateTime dataCadastro;

    public Cliente() {
    }

    public Cliente(Long id, String nome, String cnpj, String segmento, PorteEmpresa porte, String emailContato,
                   LocalDateTime dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.segmento = segmento;
        this.porte = porte;
        this.emailContato = emailContato;
        this.dataCadastro = dataCadastro;
    }

    /** CNPJ no formato 00.000.000/0000-00 para exibicao no Front-End. */
    public String getCnpjFormatado() {
        if (cnpj == null || cnpj.length() != 14) {
            return cnpj;
        }
        return cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getSegmento() { return segmento; }
    public void setSegmento(String segmento) { this.segmento = segmento; }
    public PorteEmpresa getPorte() { return porte; }
    public void setPorte(PorteEmpresa porte) { this.porte = porte; }
    public String getEmailContato() { return emailContato; }
    public void setEmailContato(String emailContato) { this.emailContato = emailContato; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return Objects.equals(cnpj, cliente.cnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cnpj);
    }

    @Override
    public String toString() {
        return "Cliente[id=" + id + ", nome=" + nome + ", cnpj=" + getCnpjFormatado() + "]";
    }
}
