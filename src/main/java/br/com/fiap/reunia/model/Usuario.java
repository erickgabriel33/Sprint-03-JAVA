package br.com.fiap.reunia.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Usuario da plataforma (tabela T_REUNIA_USUARIO).
 */
public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private String cargo;
    private String empresa;
    private LocalDateTime dataCadastro;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String senhaHash, String cargo, String empresa,
                   LocalDateTime dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.cargo = cargo;
        this.empresa = empresa;
        this.dataCadastro = dataCadastro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Usuario[id=" + id + ", nome=" + nome + ", email=" + email + ", cargo=" + cargo + "]";
    }
}
