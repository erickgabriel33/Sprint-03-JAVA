package br.com.fiap.model.entity;

import br.com.fiap.model.dto.ClienteDTO;
import jakarta.persistence.*;

@Entity(name = "Cliente")
@Table(name = "reunia_clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;
    private String nome;
    private String cnpj;
    private String segmento;
    private String porte;
    private String email;

    public Cliente() {
    }

    public Cliente(ClienteDTO clienteDTO) {
        this.nome = clienteDTO.nome();
        this.cnpj = clienteDTO.cnpj();
        this.segmento = clienteDTO.segmento();
        this.porte = clienteDTO.porte();
        this.email = clienteDTO.email();
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
