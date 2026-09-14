package br.com.fiap.model;

import br.com.fiap.exception.NegocioException;
import java.util.Objects;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String cargo;
    private String empresa;

    public Usuario() {}

    public Usuario(String nome, String email, String cargo, String empresa) throws NegocioException {
        validarCampos(nome, email);
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.empresa = empresa;
    }

    public Usuario(int id, String nome, String email, String cargo, String empresa) throws NegocioException {
        this(nome, email, cargo, empresa);
        this.id = id;
    }

    private void validarCampos(String nome, String email) throws NegocioException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new NegocioException("O nome do usuário não pode ser nulo ou vazio.");
        }
        if (email == null || !email.contains("@")) {
            throw new NegocioException("Formato de e-mail corporativo inválido: " + email);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Usuario [ID=" + id + ", Nome=" + nome + ", Email=" + email + ", Cargo=" + cargo + ", Empresa=" + empresa + "]";
    }
}